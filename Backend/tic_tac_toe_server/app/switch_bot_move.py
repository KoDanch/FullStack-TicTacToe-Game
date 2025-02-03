
import random
from functools import lru_cache

def newbee_bot_move(board):

    available_moves = available_cells(board)
    if not available_moves:
        return None  
    return random.choice(available_moves)

def defence_bot_move(board):

    for i in range(0, 9, 3):
        if board[i] == board[i+1] == 'O' and board[i+2] == '.':
            return i+2
        elif board[i] == board[i+2] == 'O' and board[i+1] == '.':
            return i+1
        elif board[i+1] == board[i+2] == 'O' and board[i] == '.':
            return i
    for i in range(3):
        if board[i] == board[i+3] == 'O' and board[i+6] == '.':
            return i+6
        elif board[i] == board[i+6] == 'O' and board[i+3] == '.':
            return i+3
        elif board[i+3] == board[i+6] == 'O' and board[i] == '.':
            return i
    if board[0] == board[4] == 'O' and board[8] == '.':
        return 8
    elif board[2] == board[4] == 'O' and board[6] == '.':
        return 6
    elif board[4] == board[6] == 'O' and board[2] == '.':
        return 2
    return random.choice([i for i, cell in enumerate(board) if cell == '.'])

def offence_bot_move(board):

    for i in range(0, 9, 3):
        if board[i] == board[i+1] == 'X' and board[i+2] == '.':
            return i+2
        elif board[i] == board[i+2] == 'X' and board[i+1] == '.':
            return i+1
        elif board[i+1] == board[i+2] == 'X' and board[i] == '.':
            return i
    for i in range(3):
        if board[i] == board[i+3] == 'X' and board[i+6] == '.':
            return i+6
        elif board[i] == board[i+6] == 'X' and board[i+3] == '.':
            return i+3
        elif board[i+3] == board[i+6] == 'X' and board[i] == '.':
            return i
    if board[0] == board[4] == 'X' and board[8] == '.':
        return 8
    elif board[2] == board[4] == 'X' and board[6] == '.':
        return 6
    elif board[4] == board[6] == 'X' and board[2] == '.':
        return 2
    return random.choice([i for i, cell in enumerate(board) if cell == '.']) 
        
def guru_bot_move(board): 

    winning_move = find_winning_move(board, 'X')
    if winning_move is not None:
        return winning_move

    block_move = find_winning_move(board, 'O')
    if block_move is not None:
        return block_move

    return random.choice([i for i, cell in enumerate(board) if cell == '.'])


def ai_move(board):
    board = list(board)
    best_score = -float('inf')
    best_moves = []
    depth = len(available_cells(board))
    
    for cell in available_cells(board):
        new_board = board.copy()
        new_board[cell] = 'O'
        score = minimax_algorithm(new_board, depth-1, False)
        
        if score > best_score:
            best_score = score
            best_moves = [cell]
        elif score == best_score:
            best_moves.append(cell)
    
    return random.choice(best_moves) if best_moves else None


def find_winning_move(board, player):
    
    for i in range(0, 9, 3):
        if board[i] == board[i+1] == player and board[i+2] == '.':
            return i+2
        elif board[i] == board[i+2] == player and board[i+1] == '.':
            return i+1
        elif board[i+1] == board[i+2] == player and board[i] == '.':
            return i
    for i in range(3):
        if board[i] == board[i+3] == player and board[i+6] == '.':
            return i+6
        elif board[i] == board[i+6] == player and board[i+3] == '.':
            return i+3
        elif board[i+3] == board[i+6] == player and board[i] == '.':
            return i
    if board[0] == board[4] == player and board[8] == '.':
        return 8
    elif board[2] == board[4] == player and board[6] == '.':
        return 6
    elif board[4] == board[6] == player and board[2] == '.':
        return 2
    return None

def minimax_algorithm(board, depth, current_turn, alpha=-float('inf'), beta=float('inf')):
    if depth == 0 or is_draw(board):
        return counter_for_minimax(tuple(board))

    cells = available_cells(board)
    
    if current_turn:
        max_eval = -float('inf')
        for cell in cells:
            new_board = board.copy()
            new_board[cell] = 'O'
            evaluation = minimax_algorithm(new_board, depth-1, False, alpha, beta)
            max_eval = max(max_eval, evaluation)
            alpha = max(alpha, evaluation)
            if beta <= alpha:
                break
        return max_eval
    else:
        min_eval = float('inf')
        for cell in cells:
            new_board = board.copy()
            new_board[cell] = 'X'
            evaluation = minimax_algorithm(new_board, depth-1, True, alpha, beta)
            min_eval = min(min_eval, evaluation)
            beta = min(beta, evaluation)
            if beta <= alpha:
                break
        return min_eval
    
def check_winner(board):

    for i in range(0, 9, 3):
        if board[i] == board[i+1] == board[i+2] != '.':
            return board[i]
    
    for i in range(3):
        if board[i] == board[i+3] == board[i+6] != '.':
            return board[i]
    
    if board[0] == board[4] == board[8] != '.':
        return board[0]
    
    if board[2] == board[4] == board[6] != '.':
        return board[2]
    
    return None
    
def make_move_on_board(board, position, player): 
    board = list(board)
    if position is not None and board[position] == '.':  
        board[position] = player
        return "".join(board)
    return board

@lru_cache(maxsize=None)
def counter_for_minimax(board):
    board = tuple(board)
    winner = check_winner(board)
    if winner == 'O': return 1
    if winner == 'X': return -1
    return 0

def is_draw(board):
    return check_winner(board) is not None or '.' not in board

def available_cells(board):
    return [i for i, cell in enumerate(board) if cell == '.']


switch_dict = {
    0: newbee_bot_move,
    1: defence_bot_move,
    2: offence_bot_move,
    3: guru_bot_move,
    4: ai_move
    }