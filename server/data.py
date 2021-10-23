import contextlib
import sqlite3
import traceback
from sqlite3 import Cursor
from typing import List, Union, Tuple, Optional

tables = {
    "users": ("user_id", "user", "password"),
    "points": ("user_id", "points_available"),
    "coupon_auction": ("coupon_id", "max_bid", "winner_id", "closeout_time", "description"),
    "bidding_log": ("user_id", "coupon_id", "points_spent", "timestamp")
}

column_types = {
    "points_available": "INTEGER",
    "points_spent": "INTEGER",
    "max_bid": "INTEGER",
    "closeout_time": "timestamp",
    "timestamp": "timestamp"
}


def are_columns_valid(table: str, check_columns: Tuple[str]):
    """
    Checks to make sure given columns exist in a given table.
    """
    if len(check_columns) == 1 and check_columns[0] == "*":
        return True
    valid_columns = tables[table]
    for col in check_columns:
        if col not in valid_columns:
            return False

    return True


class WhereConstraint:
    def __init__(self, table: str, column: str, constraint: Union[str, int, float, bool], op="=", validate=True):
        """
        Create a new WhereConstraint.

        A WhereConstraint makes for easier creation of WHERE clauses in a query. You can combine clauses with AND
        with * or & and OR with + or |.
        :param table: the table this constraint applies to. For column validity checking.
        :param column: the column this constraint applies to.
        :param constraint: the constraint upon the column. This could be a value or another column.
        :param op: the constraint type. By default this is '=', but could be replaced by any valid condition.
        """
        if validate:
            if table not in tables.keys():
                raise ValueError(f"Table {table} does not exist.")
            if column not in tables[table]:
                raise ValueError(f"{column} is not a column in table {table}. Valid tables: {table}")
        self.table = table
        self.constraint_string = f"{column} {op} {constraint}"

    @staticmethod
    def _from_string(table, string: str):
        new_constraint = WhereConstraint(table, "", "", validate=False)
        new_constraint.constraint_string = string
        return new_constraint

    def __copy__(self):
        return WhereConstraint._from_string(self.table, self.constraint_string)

    def __add__(self, other):
        if not isinstance(other, WhereConstraint):
            raise NotImplementedError
        if other.table != self.table:
            raise ValueError(f"Cannot combine Constraints for different tables")

        constraint_string = f"({self.constraint_string}) OR ({other.constraint_string})"
        return WhereConstraint._from_string(self.table, constraint_string)

    def __mul__(self, other):
        if not isinstance(other, WhereConstraint):
            raise NotImplementedError
        if other.table != self.table:
            raise ValueError(f"Cannot combine Constraints for different tables")

        constraint_string = f"({self.constraint_string}) OR ({other.constraint_string})"
        return WhereConstraint._from_string(self.table, constraint_string)

    def __or__(self, other):
        return self + other

    def __and__(self, other):
        return self * other

    def __str__(self):
        return self.constraint_string


class Database:
    def __init__(self):
        self.db = "data.db"
        # self.connection: Connection = sqlite3.connect(self.db)
        # self.cur: Cursor = self.connection.cursor()

    def execute_instructions(self, instructions: Union[str, List[str]]):
        """
        Executes a set of sqlite3 instructions. Returns a list of any results from the queries.
        """
        query_results = []
        if isinstance(instructions, str):
            instructions = [instructions]
        with contextlib.closing(
                sqlite3.connect(self.db, detect_types=sqlite3.PARSE_COLNAMES | sqlite3.PARSE_DECLTYPES)) as connection:
            with connection as cur:
                cur: Cursor = cur
                for instruction in instructions:
                    try:
                        for row in cur.execute(instruction):
                            query_results.append(row)
                    except sqlite3.OperationalError as error:
                        print(f"Encountered a problem with executing instruction: {instruction}")
                        traceback.print_exception(type(error), error, error.__traceback__)

        return query_results

    def create_tables(self):
        """
        Creates the tables defined by the global variables tables and column_types, if they don't already exist in
        the database.
        """
        def setup_columns(_columns):
            columns_text = "("
            for column in _columns:
                columns_text += f"{column} {column_types.get(column, 'TEXT')}, "
            return columns_text[:-2] + ")"

        instructions = [f"""CREATE TABLE IF NOT EXISTS {table_name}
                            {setup_columns(columns)}""" for table_name, columns in tables.items()]
        return self.execute_instructions(instructions)

    def insert_into_table(self, table: str, **column_values):
        """
        Inserts a row into a table, requiring values for every column in that table.

        Usage example: insert_into_table("table name", column_one=value, column_two=value, ...)
        """
        if table not in tables.keys():
            raise ValueError(f"Table {table} not found. Available tables: {str(list(tables.keys()))}.")

        columns: tuple = tables[table]
        if list(columns) != list(column_values.keys()):
            raise ValueError(f"Missing entries for table {table}. Expected: {str(columns)}.")

        # order values according to table definition
        values = [None] * len(columns)
        for key in column_values.keys():
            idx = columns.index(key)
            values[idx] = column_values[key]

        instruction = f"""INSERT INTO {table} VALUES {str(tuple(values))}"""
        return self.execute_instructions(instruction)

    def select_from_table(self, table: str, select_columns: Tuple[str, ...], constraint: Optional[WhereConstraint]):
        """
        Select a row or list of rows from a table.
        """
        if table not in tables.keys():
            raise ValueError(f"Table {table} not found.")
        if len(select_columns) == 0:
            raise ValueError(f"Must select at least one column.")
        if not are_columns_valid(table, select_columns):
            raise ValueError(f"Cannot select columns {select_columns} from {table}. Valid columns: {tables[table]}.")

        column_selection = ""
        for column in select_columns:
            column_selection += f"{column}, "

        instruction = f"""SELECT {column_selection[:-2]} FROM {table}"""
        if constraint:
            instruction += f" WHERE {str(constraint)}"
        return self.execute_instructions(instruction)

    def update_table_rows(self, table: str, constraint: Optional[WhereConstraint], **column_values):
        """
        Update rows fitting the constraint in a table with the given values.
        """
        if table not in tables.keys():
            raise ValueError(f"Table {table} not found. Available tables: {str(list(tables.keys()))}.")
        if len(column_values) == 0:
            raise ValueError(f"Nothing to update with.")
        if not are_columns_valid(table, tuple(column_values.keys())):
            raise ValueError(
                f"Cannot update columns {tuple(column_values.keys())} from {table}. Valid columns: {tables[table]}.")

        set_clause = ""
        for column, value in column_values.items():
            set_clause += f"{column} = {value}, "

        instruction = f"""UPDATE {table} SET {set_clause[:-2]}"""
        if constraint:
            instruction += f" WHERE {str(constraint)}"
        return self.execute_instructions(instruction)

    def delete_table_rows(self, table: str, constraint: WhereConstraint):
        """
        Delete rows fitting the given constraint from the given table.
        """
        if table not in tables.keys():
            raise ValueError(f"Table {table} not found. Available tables: {str(list(tables.keys()))}.")
        instruction = f"""DELETE FROM {table}"""
        if constraint:
            instruction += f" WHERE {str(constraint)}"
        return self.execute_instructions(instruction)

    def select_user_tuple(self, username):
        """
        An alias for getting the row containing a specific username from the users table.
        """
        return self.select_from_table("users",
                                      ("user_id", "user", "password"),
                                      WhereConstraint("users", "user", f"'{username}'"))
