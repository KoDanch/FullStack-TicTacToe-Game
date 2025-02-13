from flask import Blueprint, jsonify, request
from sqlalchemy.exc import IntegrityError
from app import db
from datetime import datetime
from sqlalchemy import text
from werkzeug.utils import secure_filename
from .switch_bot_move import switch_dict, check_winner, is_draw, make_move_on_board, newbee_bot_move
from app.models import User, Apartment, Game, Ranking, PhotoReport, Tournament
import logging, random, json, uuid, os, hashlib


logging.basicConfig(level=logging.DEBUG)

ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}


main = Blueprint('main', __name__)

@main.route('/')
def home(): 
    return jsonify({"message": "Welcome to Tic Tac API"})

@main.route('/test_db', methods=['GET'])
def test_db():
    try:
        db.session.execute(text('SELECT 1'))
        return jsonify({"message": "Database connection successful"})
    except Exception as e:
        return jsonify({"message": f"Database connection failed: {str(e)}"}), 500


@main.route('/register', methods=['POST'])
def register(): 
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')
    street = data.get('street')
    city = data.get('city')
    state = data.get('state')
    zipcode = data.get('zipcode')

    if not username or not password: 
        return jsonify({"message": "Username and password are required"}), 400
    
    try:
        ex_user = db.session.query(User).filter_by(username = username).first()
        print(f"Checking user existence: {username} - Found: {ex_user}")
        if ex_user: 
            print("User exists, returning error")
            return jsonify({"message": f"User {username} has been exist"}), 400 

        print(f"Checking user existence: {username}")
        print(data)
        new_address = Address(
            street = street,
            city = city,
            state = state,
            postal_code = zipcode
        )
        db.session.add(new_address)
        db.session.commit()

        new_user = User(username = username, address_id = new_address.id)
        new_user.set_password(password)
        db.session.add(new_address)
        db.session.add(new_user)
        db.session.commit()
        
        return jsonify({"message": f"User {username} registered successfully"}), 201
    except IntegrityError as ex: 
        db.session.rollback()
        print(f"IntegrityError occurred: {str(ex)}")
        return jsonify ({"message": f"Username '{username}' already exist"}), 400
    except Exception as ex:
        db.session.rollback()
        print(f"An error occurred: {str(ex)}")
        return jsonify({"message": f"An error occurred: {str(ex)}"}), 500
    
@main.route('/login', methods=['POST'])
def login():
        data = request.get_json()

        username = data.get('username')
        password = data.get('password')

        if not username or not password: 
            return jsonify({"message": "Username and password are required"}), 400

        try: 
            user = db.session.query(User).filter_by(username = username).first()

            if user and user.check_password(password):

                if user.user_deleted:
                    return jsonify({"message": "User has been deleted"}), 400

                hashed_info = f"{user.username}|{user.password}|{datetime.now().isoformat()}"
                token_data = hashlib.sha256(hashed_info.encode())
                hashed_token = token_data.hexdigest()

                user.token = hashed_token
                db.session.commit()
                return jsonify({"message": "Login success", "auth_token": hashed_token}), 200
            else:
                return jsonify({"message": "Invalid username or password"}), 401

        except Exception as ex: 
            db.session.rollback()
            print( f"An error occurated: {str(ex)}")
            return jsonify({"message": f"An error occurated: {str(ex)}"}), 500

@main.route('/get_user_data', methods=['POST'])
def get_user_data():
    data = request.get_json()
    token = data.get('auth_token')
    print(f"token: {token}")
    try:
        user = db.session.query(User).filter_by(token = token).first()

        if user: 
            user_data = {
                "username": user.username,
                "player_draws": user.games_draws,
                "player_loses": user.games_loses,
                "player_wins": user.games_wins,
                "rating": user.rating,
                "address": {
                    "street": user.address.street,
                    "city": user.address.city,
                    "state": user.address.state,
                    "postal_code": user.address.postal_code
                }
            }
            return jsonify({"message": "Success", "user_data": user_data}), 200
        else:
            return jsonify({"message": "User not found"}), 404

    except Exception as ex: 
            db.session.rollback()
            print(f"IntegrityError occurred: {str(ex)}")
            return jsonify({"message": f"An error occurated: {str(ex)}"}), 500

@main.route('/edit_data_or_delete_user', methods=['POST'])
def edit_data_or_delete_user():
    data = request.get_json()
    token = data.get('auth_token')
    password = data.get('password')
    username = data.get('username')
    delete_user = data.get('user_deleted')
    address_data = data.get('address')
    
    print(f"addr: {address_data}")
    print(f"addr: {password}")
    print(f"addr: {username}")
    

    try:
        user = db.session.query(User).filter_by(token=token).first()

        if user and user.check_password(password):

            if delete_user:
                if delete_user == 1:
                    user.user_deleted = True
                    db.session.commit()
                    return jsonify({"message": "User deleted successfuly"}), 200 
                else: 
                    user.user_deleted = False 
                    return jsonify({"message": "Failed deleting user"}), 404

            if username: 
                user.username = username

            
            if address_data:
                user.address.street = address_data.get('street', user.address.street)
                user.address.city = address_data.get('city', user.address.city)
                user.address.state = address_data.get('state', user.address.state)
                user.address.postal_code = address_data.get('postal_code', user.address.postal_code)
            
            db.session.commit()
            update_leaderboard()
            return jsonify({"message": "Data successfuly updated"}), 200
        else: 
            return jsonify({"message": "Session expired or not valid password"}), 404 
    
    except Exception as ex: 
        db.session.rollback()
        return jsonify({"message": f"An error occurated: {str(ex)}"}), 500


@main.route('/start_game', methods=['POST'])
def start_game():
    data = request.get_json()
    token = data.get('auth_token')
    location = data.get('player_location')
    current_turn = data.get('current_turn')
    difficulty = data.get('difficulty')

    print(f"location: {str(location)}")
    user = User.query.filter_by(token = token).first()

    if user is None:
        return jsonify({"message": "Auth token not valid"}), 401
    
    
    game = Game(player_id=user.id, location=location, game_date = datetime.now(), board=".........")

    if not current_turn:
        current_turn = random.choice(["X", "O"])

    if (current_turn == "O"):
        bot_move_position = selected_difficulty(difficulty, game.board) 
        new_board = make_move_on_board(game.board, bot_move_position, 'O')
        game.board = new_board

    db.session.add(game)
    db.session.commit()
    return jsonify({"game_id": game.id, "board": game.board}),201

@main.route('/make_move/<int:game_id>', methods=['POST'])
def make_move(game_id):
    data = request.get_json()
    position = data.get('position')
    player = data.get('player')
    difficulty = data.get('difficulty')
    print(difficulty)
    
    game = Game.query.get(game_id)
    if not game:
        return jsonify({"message": "Game not found"}), 404

    db.session.commit()
    
    board = game.board
    new_board = make_move_on_board(board, position, player)
    game.board = new_board
    
    player_id = game.player_id
    winner = check_winner(new_board)
    if winner:
        game.winner = winner
        game.result = 2 if winner == player else 0 
        db.session.commit()
        update_player_stats(player_id, game.result)
        return jsonify({"board": new_board, "winner": game.result, "current_turn": "O"}), 200

    if not winner and is_draw(new_board):
        game.result = 1 
        db.session.commit()
        update_player_stats(player_id, game.result)
        return jsonify({"board": new_board, "winner": game.result, "current_turn": "O"}), 200

    if game.current_turn == 'X':  
        bot_move_position = selected_difficulty(difficulty, new_board)
        if bot_move_position is None:
            return jsonify({"message": "No available moves left, the game is over!"})  
        new_board = make_move_on_board(new_board, bot_move_position, 'O')
        game.board = new_board
        game.current_turn = 'X'
        

    winner = check_winner(new_board)
    if winner:
        game.winner = winner
        game.result = 0 if winner == 'O' else 2
        db.session.commit()
        update_player_stats(player_id, game.result)
        return jsonify({"board": new_board, "winner": game.result, "current_turn": "X"}), 200

    if not winner and is_draw(new_board):
        game.result = 1 
        db.session.commit()
        update_player_stats(player_id, game.result)
        return jsonify({"board": new_board, "winner": game.result, "current_turn": "X"}), 200
    
    db.session.commit()

    return jsonify({"board": new_board, "current_turn": game.current_turn}), 200

@main.route('/get_help_move/<int:game_id>', methods=['GET'])
def get_help_move(game_id):

    game = Game.query.get(game_id)

    if not game:
        return jsonify({"message": "Game not found"}), 404
    
    board = game.board
    help_bot_move = newbee_bot_move(board)
    if help_bot_move is None:
        return jsonify({"message": "Game over"}), 201
    help_board = make_move_on_board(board, help_bot_move, "X")

    return jsonify({"help_board": help_board})

@main.route('/get_leaderboard_data', methods=['GET'])
def get_leaderboard_data():
    ranks = Ranking.query.all()

    leaderboard = []
    for idx, rank in enumerate(ranks[:5], start=1):
        leaderboard.append({
            "rank": idx,
            "username": rank.username,
            "player_wins": rank.player_wins,
            "player_draws": rank.player_draws,
            "player_loses": rank.player_loses,
            "win_rate": rank.win_rate,
        })

    response = {
        "leaderboard": leaderboard,
    }
    return jsonify(response), 200

@main.route('/photo_report_upload', methods=['POST'])
def photo_report_upload():
   
    data = request.form.get('data')

    try:
        data_json = json.loads(data)  
        token = data_json.get('auth_token')
        trigger_create_tournament = data_json.get('photo_num', 0)
    except (ValueError, KeyError):
        return jsonify({"message": "Not valid data"}), 400
    
    print(token)

    user = User.query.filter_by(token=token).first()

    if not user:
        return jsonify({"message": "Auth token not valid"}), 401
    
    if 'file' not in request.files:
        return jsonify({"message": "No file part"}), 400
    
    file = request.files['file']
    
    if file.filename == '':
        return jsonify({"message": "No selected file"}), 400
    
    tournament_id = None
    UPLOAD_FOLDER = None
    if trigger_create_tournament == 1:
        new_tournament = Tournament(player_id=user.id, tournament_date=datetime.now())
        db.session.add(new_tournament)
        db.session.commit()
        UPLOAD_FOLDER = f"uploads/Tournament/{str(user.username).strip()}/tournament_{str(new_tournament.id)}"
        create_tournament_folder(UPLOAD_FOLDER)
        recent_games = Game.query.filter_by(player_id=user.id, tournament_id=None).order_by(Game.game_date.desc()).limit(5).all()

        for game in recent_games:
            game.tournament_id = new_tournament.id
            tournament_id = new_tournament.id

        db.session.commit()
    else:
        last_tournament = Tournament.query.filter_by(player_id=user.id).order_by(Tournament.tournament_date.desc()).first()

        if last_tournament:
            tournament_id = last_tournament.id
        
        UPLOAD_FOLDER = f"uploads/Tournament/{str(user.username).strip()}/tournament_{str(last_tournament.id)}"
    
    if file and allowed_file(file.filename):

        filename = secure_filename(file.filename)
        unique_filename = f"{uuid.uuid4().hex}_{filename}"
        save_path = os.path.join(UPLOAD_FOLDER, unique_filename)
        file.save(save_path)


        report = PhotoReport(filename=filename,file_path=save_path, tournament_id=tournament_id, player_id = user.id)
        db.session.add(report)
        db.session.commit()
        return jsonify({"message": f"Success upload"}), 200

    return jsonify({"message": "File type not allowed"}), 400
    
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def create_tournament_folder(newFolder):
    if not os.path.exists(newFolder):
        os.makedirs(newFolder)
    else:
        print("Folder is exist")

def update_leaderboard():
    users = User.query.filter_by(user_deleted=False).all()

    if not users:
        print("Error getting users")

    user_ranking = []
    for user in users:
        win_ratio = user.games_wins / user.games_played if user.games_played > 0 else 0
        user_ranking.append((user, win_ratio))

    top_users = sorted(user_ranking, key=lambda x: x[1], reverse=True)

    Ranking.query.delete()
    db.session.commit()
    db.session.execute(text("DBCC CHECKIDENT ('ranking', RESEED, 0);"))
    for idx, (user, win_ratio) in enumerate(top_users, start=1):
        ranking_entry = Ranking(
            player_id = user.id,
            username = user.username,
            player_wins = user.games_wins,
            player_draws = user.games_draws,
            player_loses = user.games_loses,
            win_rate=int(win_ratio * 100)
        )
        user.rating = idx
        db.session.add(ranking_entry)
    
    db.session.commit()
    
def update_player_stats(player_id, result):
    player = User.query.get(player_id)
    if not player: 
        print("Player not found")

    if result == 0:
        player.games_loses += 1
    elif result == 1:
        player.games_draws += 1
    elif result == 2:
        player.games_wins += 1

    player.games_played += 1
    db.session.commit()
    update_leaderboard()

    print("Player stats updated success")

def selected_difficulty(difficulty, board):
    bot_move = switch_dict.get(difficulty)

    if bot_move:
        return bot_move(board) 
    else:
        print("Not valid selected difficulty")
