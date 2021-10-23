import contextlib
import sqlite3
import traceback
from sqlite3 import Cursor
from typing import List, Union

tables = {
    "users": ("user_id", "user", "password"),
    "points": ("user_id", "points_available"),
    "coupon_auction": ("coupon_id", "max_bid", "winner_id", "closeout_time"),
    "bidding_log": ("bid_id", "user_id", "coupon_id", "points_spent", "timestamp")
}


class Database:
    def __init__(self):
        self.db = "data.db"
        # self.connection: Connection = sqlite3.connect(self.db)
        # self.cur: Cursor = self.connection.cursor()

    def execute_instructions(self, instructions: Union[str, List[str]]):
        query_results = []
        if isinstance(instructions, str):
            instructions = [instructions]
        with contextlib.closing(sqlite3.connect(self.db)) as connection:
            with connection as cur:
                cur: Cursor = cur
                for instruction in instructions:
                    try:
                        for row in cur.execute(instruction):
                            query_results.append(row)
                    except sqlite3.OperationalError as error:
                        print(f"Encountered a problem with executing instruction: {instruction}")
                        traceback.print_exception(type(error), error, error.__traceback__)
                        query_results = False

        if len(query_results) == 1:
            return query_results[0]

        return query_results

    def create_table(self):
        instructions = [f"""CREATE TABLE {table_name}
                            {str(columns)}""" for table_name, columns in tables.items()]
        return self.execute_instructions(instructions)

    def insert_into_table(self, table: str, **kwargs):
        """
        Usage example: insert_into_table("table name", column_one=value, column_two=value, ...)
        """
        if table not in tables:
            raise ValueError(f"Table {table} not found. Available tables: {str(list(kwargs.keys()))}.")

        columns = tables[table]
        if list(columns) != list(kwargs.keys()):
            raise ValueError(f"Missing entries for table {table}. Expected: {str(list(kwargs.keys()))}.")

        instruction = f"""INSERT INTO {table} VALUES {str(tuple(kwargs.values()))}"""
        return self.execute_instructions(instruction)

    def select_user_tuple(self, username):
        instructions = f"""SELECT user_id, user, password FROM users WHERE user = '{username}'"""
        row = self.execute_instructions(instructions)
        return row

    # Select Statements probably not needed b/c self.execute_instructions()
    def select_user_table(self, user_id, user):
        pass

    def select_points_table(self, user_id):
        pass

