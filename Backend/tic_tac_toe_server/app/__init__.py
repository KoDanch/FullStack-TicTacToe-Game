from flask import Flask 
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import text
from .procedures import create, drop

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
        
        for drop_item in drop.drop_query_exec:
            if drop_item.strip():
                db.session.execute(text(drop_item))
                db.session.commit()

        for name_item, create_item in create.procedures.items():
            db.session.execute(text(create_item))
            db.session.commit()

    from .routes import main
    app.register_blueprint(main)

    return app