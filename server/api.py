from flask import Flask
from flask_restful import Resource, Api, reqparse


app = Flask(__name__)
api = Api(app)


class AuthenticationAPI(Resource):
    def __init__(self):
        self.parser = reqparse.RequestParser()
        self.parser.add_argument('username', type=str, required=True)
        self.parser.add_argument('password', type=str, required=True)
        super().__init__()


class LoginValidator(AuthenticationAPI):
    def post(self):
        # validate a login
        pass


class RegistrationHandler(AuthenticationAPI):
    def post(self):
        # register a user
        pass


class BiddingAPI(Resource):
    def get(self):
        # gets a user's bids
        pass

    def put(self):
        # creates a new bid onto the database for the user
        pass

    def patch(self):
        # patches a user's existing bid
        pass


class CouponAPI(Resource):
    def get(self):
        # gets the auction's coupon information
        pass


class PointsAPI(Resource):
    def get(self):
        # gets a user's bidding points
        pass

    def patch(self):
        # updates a user's bidding points
        pass


api.add_resource(LoginValidator, '/login')
api.add_resource(RegistrationHandler, '/register')
api.add_resource(BiddingAPI, '/bidding')
api.add_resource(CouponAPI, '/coupons')
api.add_resource(PointsAPI, '/points')


if __name__ == "__main__":
    app.run(debug=True)
