from app import db
from werkzeug.security import generate_password_hash, check_password_hash
from datetime import datetime

class User(db.Model):
    __tablename__ = 'user'

    id = db.Column(db.Integer, primary_key=True)
    token = db.Column(db.String(64))
    username = db.Column(db.String(50), unique=True, nullable=False)
    password = db.Column(db.String(255), nullable=False)
    user_deleted = db.Column(db.Boolean, default=False)
    games_played = db.Column(db.Integer, default=0)
    games_wins = db.Column(db.Integer, default=0)
    games_loses = db.Column(db.Integer, default=0)
    games_draws = db.Column(db.Integer, default=0)
    rating = db.Column(db.Integer, default=0)

    address_id = db.Column(db.Integer, db.ForeignKey('address.id'), nullable=False)
    requisite_id = db.Column(db.Integer, db.ForeignKey('user_requisite.id'))

    address = db.relationship('Address', backref=db.backref('users', lazy='dynamic'))
    requisite = db.relationship('UserRequisite', backref=db.backref('user', lazy=True))

    def __repr__(self): 
        return f"<User {self.username}>"
    
    def set_password(self, password): 
        self.password = generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.password, password) 

class City(db.Model):
    __tablename__ = 'city'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50), unique=True, nullable=False)

class Street(db.Model):
    __tablename__ = 'street'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    city_id = db.Column(db.Integer, db.ForeignKey('city.id'), nullable=False)

    city = db.relationship('City', backref=db.backref('streets', lazy=True))

class House(db.Model):
    __tablename__ = 'house'

    id = db.Column(db.Integer, primary_key=True)
    number = db.Column(db.String(10), nullable=False)
    street_id = db.Column(db.Integer, db.ForeignKey('street.id'), nullable=False)

    street = db.relationship('Street', backref=db.backref('houses', lazy=True))

class Apartment(db.Model):
    __tablename__ = 'apartment'

    id = db.Column(db.Integer, primary_key=True)
    number_apartment = db.Column(db.String(4))
    house_id = db.Column(db.Integer, db.ForeignKey('house.id'), nullable=False)

    house = db.relationship('House', backref=db.backref('apartments', lazy=True))

class Address(db.Model):
    __tablename__ = 'address'

    id = db.Column(db.Integer, primary_key=True)
    house_id = db.Column(db.Integer, db.ForeignKey('house.id'), nullable=False)
    apartment_id = db.Column(db.Integer, db.ForeignKey('apartment.id'))  

    house = db.relationship('House', backref=db.backref('addresses', lazy=True))
    apartment = db.relationship('Apartment', backref=db.backref('addresses', lazy=True))

class UserRequisite(db.Model):
    __tablename__='user_requisite'

    id = db.Column(db.Integer, primary_key=True)
    full_name = db.Column(db.String(50, collation="Cyrillic_General_100_CS_AS"))
    phone_number = db.Column(db.String(13))
    email = db.Column(db.String(25))

class Game(db.Model):
    __tablename__ = 'game'
    
    id = db.Column(db.Integer, primary_key=True)
    player_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    current_turn = db.Column(db.String(2), default='X')
    board = db.Column(db.String(9), default=".........", nullable=False)
    winner = db.Column(db.String(10))
    result = db.Column(db.Integer)
    game_date = db.Column(db.DateTime, default=datetime.now())
    location = db.Column(db.String(255))
    tournament_id = db.Column(db.Integer, db.ForeignKey('tournament.id'), nullable=True)

    player = db.relationship('User', backref=db.backref('games', lazy=True))
    tournament = db.relationship('Tournament', backref=db.backref('games', lazy=True))

class Ranking(db.Model):
    __tablename__ = 'ranking'

    id = db.Column(db.Integer, primary_key = True) 
    player_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False) 
    username = db.Column(db.String(50))
    player_wins = db.Column(db.Integer)
    player_draws = db.Column(db.Integer)
    player_loses = db.Column(db.Integer)
    win_rate = db.Column(db.Integer, default=0)

    player = db.relationship('User', backref=db.backref('rankings', uselist=False))

class PhotoReport(db.Model):
    __tablename__='photo_report'

    id = db.Column(db.Integer, primary_key = True)
    filename = db.Column(db.String, nullable = False)
    file_path = db.Column(db.String, nullable = False)
    tournament_id = db.Column(db.Integer, db.ForeignKey('tournament.id'), nullable=False)
    player_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)

    player = db.relationship('User', backref=db.backref('photo_reports', lazy=True))
    tournament = db.relationship('Tournament', backref=db.backref('photo_reports', lazy=True))

class Tournament(db.Model):
    __tablename__='tournament'

    id = db.Column(db.Integer, primary_key=True)
    player_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    tournament_date = db.Column(db.DateTime, default=datetime.now())

    player = db.relationship('User', backref=db.backref('tournaments', lazy=True))
