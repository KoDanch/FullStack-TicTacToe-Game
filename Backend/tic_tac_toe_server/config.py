import os

class Config: 
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    SECRET_KEY = os.urandom(24)

    SQLALCHEMY_DATABASE_URI = (
        'mssql+pyodbc://user1:sa@localhost/TicTacToe?driver=ODBC+Driver+17+for+SQL+Server'
        )