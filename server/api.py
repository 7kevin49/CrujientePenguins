from functools import wraps
from uuid import uuid4

from flask import Flask, request
from flask_restful import Resource, Api, reqparse
from werkzeug.security import generate_password_hash, check_password_hash
import jwt

import data

app = Flask(__name__)
app.config["SECRET_KEY"] = "CrujientePenguins"      # This is the key used to encrypt JSON Web Tokens (JWTs)
api = Api(app, prefix="/api/v1")
database = data.Database()


"""
The following methods are for JWT Authentication.
"""


def requires_jwt(f):
    """
    A decorator that enforces an authorization policy for API requests. Requests to these methods must include a
    valid JWT auth_token in their headers.

    API calls decorated with this will receive the contents of the decoded token in their **kwargs.
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


"""
The following classes serve as API endpoint resources for the server.
"""


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
        _data = database.select_user_tuple(username)
        if _data:
            uuid, _, hashed_password = _data
            if check_password_hash(hashed_password, password):
                token_payload = {
                    "uuid": uuid
                }
                token = jwt.encode(token_payload, app.config["SECRET_KEY"])
                return {"token": token.decode("UTF-8")}

        return {"message": "invalid login"},  401


class RegistrationAPI(AuthenticationAPI):
    def post(self):
        # register a user
        uuid = str(uuid4())
        args = self.parser.parse_args()
        hashed_password = generate_password_hash(args["password"])
        if not database.insert_into_table("users", user_id=uuid, user=args["username"], password=hashed_password):
            return {"message": "a problem occurred"}, 404


class BiddingAPI(Resource):
    @requires_jwt
    def get(self, *args, **kwargs):
        # gets a user's bids
        pass

    @requires_jwt
    def put(self, *args, **kwargs):
        # creates a new bid onto the database for the user
        pass

    @requires_jwt
    def patch(self, *args, **kwargs):
        # patches a user's existing bid
        pass


class CouponAPI(Resource):
    def get(self, *args, **kwargs):
        # gets the auction's coupon information
        pass


class PointsAPI(Resource):
    @requires_jwt
    def get(self, *args, **kwargs):
        # gets a user's bidding points
        pass

    @requires_jwt
    def patch(self, *args, **kwargs):
        # updates a user's bidding points
        pass


class TestAPI(Resource):
    def get(self, *args, **kwargs):
        # checks server is up
        return {"Hello": "world!"}

    def post(self, *args, **kwargs):
        pass


api.add_resource(LoginAPI, '/login')
api.add_resource(RegistrationAPI, '/register')
api.add_resource(BiddingAPI, '/bidding')
api.add_resource(CouponAPI, '/coupons')
api.add_resource(PointsAPI, '/points')
api.add_resource(TestAPI, '/test')


def main():
    # database.create_table()
    app.run(debug=True, use_reloader=False)


if __name__ == "__main__":
    main()
