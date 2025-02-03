from flask import Flask 
from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()
from app.models import User, Game, Ranking

def create_app():
    app = Flask(__name__)
    app.config.from_object('config.Config')

    db.init_app(app)

    with app.app_context():
        print("Conection to database...")
        db.create_all()
        print("Tables created successfully.")

    from .routes import main
    app.register_blueprint(main)

    return app