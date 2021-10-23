import datetime
from functools import wraps
from random import choice
from uuid import uuid4

import jwt
import yaml
from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.triggers.cron import CronTrigger
from flask import Flask, request
from flask_restful import Resource, Api, reqparse, fields, marshal
from werkzeug.security import generate_password_hash, check_password_hash

from data import Database, WhereConstraint

with open("apiconfig.yml", "r") as ymlfile:
    config = yaml.safe_load(ymlfile)

#
# The following variables are for the API configuration
#

app = Flask(__name__)
api = Api(app, prefix="/api/v1")
database = Database()

bid_fields = {
    "user_id": fields.String,
    "coupon_id": fields.String,
    "points_spent": fields.Integer,
    "timestamp": fields.DateTime
}

coupon_fields = {
    "id": fields.String,
    "max_bid": fields.Integer,
    "winner_id": fields.String,
    "closeout_time": fields.DateTime,
    "description": fields.String
}

# This is the key to encrpy the JSON Web Tokens with
app.config["SECRET_KEY"] = config["api"].get("SECRET_KEY", "CrujientePenguins")

#
# The following constants are for configuring the points economy
#

# Points given to newly registered users
BASE_POINTS = config["economy"].get("base_points", 100)

# Auction data
NUM_AUCTIONS = config["economy"].get("num_active_auctions", 5)
COUPON_DESCRIPTIONS = config["economy"].get("coupon_descriptions", [""])


#
# The following methods are for JWT Authentication.
#

def requires_jwt(f):
    """
    A decorator that enforces an authorization policy for API requests. Requests to these methods must include a
    valid JWT auth_token in their headers.

    API calls decorated with this will have access the contents of the decoded token with their **kwargs.
    """

    @wraps(f)
    def decorator(*args, **kwargs):
        token = None

        if 'auth_token' in request.headers:
            token = request.headers['auth_token']

        if not token:
            return {'message': 'missing auth_token in headers'}, 401

        try:
            token_data = jwt.decode(token, app.config["SECRET_KEY"])
            kwargs.update(**token_data)
        except jwt.DecodeError:
            return {'message': 'invalid auth_token'}, 401

        return f(*args, **kwargs)

    return decorator


#
# The following classes serve as API endpoint resources for the server.
#

class AuthenticationAPI(Resource):
    def __init__(self):
        self.parser = reqparse.RequestParser()
        self.parser.add_argument("username", required=True)
        self.parser.add_argument("password", required=True)
        super().__init__()


class LoginAPI(AuthenticationAPI):
    def post(self):
        # login a user, return a JWT
        args = self.parser.parse_args()
        username = args["username"]
        password = args["password"]
        _data = database.select_user_tuple(username)[0]
        if _data:
            uuid, _, hashed_password = _data
            if check_password_hash(hashed_password, password):
                token_payload = {
                    "uuid": uuid
                }
                token = jwt.encode(token_payload, app.config["SECRET_KEY"])
                return {"token": token.decode("UTF-8")}

        return {"message": "invalid login"}, 401


class RegistrationAPI(AuthenticationAPI):
    def post(self):
        # register a user
        uuid = str(uuid4())
        args = self.parser.parse_args()
        hashed_password = generate_password_hash(args["password"])

        # Attempt to register the new user
        database.insert_into_table("users", user_id=uuid, user=args["username"], password=hashed_password)
        # Give the user some points to start out with
        database.insert_into_table("points", user_id=uuid, points_available=BASE_POINTS)


class BiddingAPI(Resource):
    def __init__(self):
        self.parser = reqparse.RequestParser()
        self.parser.add_argument("coupon_id", required=True)
        self.parser.add_argument("bid_amount", required=True, type=int)
        super().__init__()

    @requires_jwt
    def get(self, *args, **kwargs):
        # gets a user's bids
        uuid = kwargs["uuid"]
        bids = database.select_from_table("bidding_log",
                                          ("*",),
                                          WhereConstraint("bidding_log", "user_id", f"'{uuid}'"))
        converted_bids = []
        for bid in bids:
            converted_bids.append({
                "user_id": bid[0],
                "coupon_id": bid[1],
                "points_spent": bid[2],
                "timestamp": bid[3]
            })
        return marshal({"bids_made": converted_bids}, {"bids_made": fields.List(fields.Nested(bid_fields))})

    @requires_jwt
    def put(self, *args, **kwargs):
        # creates a new bid onto the database for the user
        uuid = kwargs["uuid"]
        args = self.parser.parse_args()
        coupon_id = args["coupon_id"]
        bid_amount = args["bid_amount"]

        points = database.select_from_table("points",
                                            ("points_available",),
                                            WhereConstraint("points", "user_id", f"'{uuid}'"))[0][0]
        points -= bid_amount
        if points < 0:
            return {"message": "Cannot bid more points than available!"}, 400

        # set the bid
        database.insert_into_table("bidding_log",
                                   user_id=uuid,
                                   coupon_id=coupon_id,
                                   points_spent=bid_amount,
                                   timestamp=str(datetime.datetime.now()))
        # remove points
        database.update_table_rows("points",
                                   WhereConstraint("points", "user_id", f"'{uuid}'"),
                                   points_available=points)

        self.update_coupon_winner(coupon_id, bid_amount, uuid)

    @requires_jwt
    def patch(self, *args, **kwargs):
        # patches a user's existing bid
        uuid = kwargs["uuid"]
        args = self.parser.parse_args()
        coupon_id = args["coupon_id"]
        bid_amount = args["bid_amount"]

        constraint = WhereConstraint(
            "bidding_log",
            "user_id",
            f"'{uuid}'") & WhereConstraint(
            "bidding_log",
            "coupon_id",
            f"'{coupon_id}'")

        previous_bid = database.select_from_table(
            "bidding_log",
            ("points_spent",),
            constraint
        )[0][0]

        delta_bid = bid_amount - previous_bid
        if delta_bid < 0:
            return {"message": "Cannot bid less than a previous bid!"}, 400

        points = database.select_from_table("points",
                                            ("points_available",),
                                            WhereConstraint("points", "user_id", f"'{uuid}'"))[0][0]
        points -= delta_bid
        if points < 0:
            return {"message": "Cannot bid more points than available!"}, 400

        # update the bid
        database.update_table_rows("bidding_log",
                                   constraint,
                                   user_id=f"'{uuid}'",
                                   coupon_id=coupon_id,
                                   points_spent=bid_amount,
                                   timestamp=f"'{str(datetime.datetime.now())}'")
        # remove points
        database.update_table_rows("points",
                                   WhereConstraint("points", "user_id", f"'{uuid}'"),
                                   points_available=points)

        self.update_coupon_winner(coupon_id, bid_amount, uuid)

    def update_coupon_winner(self, coupon_id, this_bid, uuid):
        previous_max_bid = database.select_from_table(
            "coupon_auction",
            ("max_bid",),
            WhereConstraint("coupon_auction", "coupon_id", f"'{coupon_id}'")
        )[0][0]
        if this_bid <= previous_max_bid:
            return

        database.update_table_rows(
            "coupon_auction",
            WhereConstraint("coupon_auction", "coupon_id", f"'{coupon_id}'"),
            max_bid=this_bid,
            winner_id=f"'{uuid}'"
        )


class CouponAPI(Resource):
    def __init__(self):
        self.parser = reqparse.RequestParser()
        self.parser.add_argument("include_old", default=False)

    def get(self, *args, **kwargs):
        # gets the auction's coupon information
        args = self.parser.parse_args()
        include_old = args["include_old"]
        if include_old:
            coupons = database.select_from_table("coupon_auction", ("*",), None)
        else:
            coupons = database.select_from_table(
                "coupon_auction",
                ("*", ),
                WhereConstraint("coupon_auction", "closeout_time", f"'{str(datetime.datetime.now())}'", ">")
            )
        converted_coupons = []
        for coupon in coupons:
            converted_coupons.append({
                "id": coupon[0],
                "max_bid": coupon[1],
                "winner": coupon[2],
                "closeout_time": coupon[3],
                "description": coupon[4],
            })
        return marshal({"coupon_auction": converted_coupons},
                       {"coupon_auction": fields.List(fields.Nested(coupon_fields))})


class PointsAPI(Resource):
    def __init__(self):
        self.parser = reqparse.RequestParser()
        self.parser.add_argument("delta_points", required=True, type=int)

    @requires_jwt
    def get(self, *args, **kwargs):
        # gets a user's bidding points
        uuid = kwargs["uuid"]
        points = database.select_from_table("points",
                                            ("points_available",),
                                            WhereConstraint("points", "user_id", f"'{uuid}'"))[0][0]
        return {"points_available": points}

    @requires_jwt
    def patch(self, *args, **kwargs):
        # Adds points to a user's bid
        uuid = kwargs["uuid"]
        args = self.parser.parse_args()
        delta_points = args["delta_points"]
        current_points = database.select_from_table("points",
                                                    ("points_available",),
                                                    WhereConstraint("points", "user_id", f"'{uuid}'"))[0][0]
        next_points = current_points + delta_points
        database.update_table_rows("points",
                                   WhereConstraint("points", "user_id", f"'{uuid}'"),
                                   points_available=next_points)


api.add_resource(LoginAPI, '/login')
api.add_resource(RegistrationAPI, '/register')
api.add_resource(BiddingAPI, '/bidding')
api.add_resource(CouponAPI, '/coupons')
api.add_resource(PointsAPI, '/points')


#
# The following methods implement regularly scheduled auction updates
#

def check_auctions():
    """
    Handles closing auctions and determining how many new auctions to make.
    """
    auctions = database.select_from_table(
        "coupon_auction",
        ("*",),
        None
    )
    current_time = datetime.datetime.now()
    stale_auctions = list(filter(lambda auction: auction[-2] <= current_time, auctions))
    for coupon_id, max_bid, winner_id, closeout_time, description in stale_auctions:
        # TODO pick winner, clear bidding log of these bids
        continue

    auctions_deficit = NUM_AUCTIONS - len(auctions) + len(stale_auctions)
    if auctions_deficit > 0:
        create_coupons(auctions_deficit)


def create_coupons(n: int):
    """
    Creates n new coupon auctions.
    """
    max_bid = 0
    winner_id = "''"
    now = datetime.datetime.now()
    closeout_time = datetime.datetime(now.year, now.month, now.day, 0, 0, 0, 0) + datetime.timedelta(days=1)
    for _ in range(n):
        description = choice(COUPON_DESCRIPTIONS)
        coupon_id = str(uuid4())
        database.insert_into_table(
            "coupon_auction",
            coupon_id=coupon_id,
            max_bid=max_bid,
            winner_id=winner_id,
            closeout_time=str(closeout_time),
            description=description
        )


auction_scheduler = BackgroundScheduler()
auction_scheduler.add_job(check_auctions, trigger=CronTrigger(second=0))


def main():
    database.create_tables()
    auction_scheduler.start()
    check_auctions()
    app.run(debug=True, use_reloader=False)


if __name__ == "__main__":
    main()
