from . import create

drop_query_exec = []

for drop_procedure_name, create_procedure in create.procedures.items():
    drop_query_exec.append(f"\
        IF OBJECT_ID('{drop_procedure_name}', 'P') IS NOT NULL\
            DROP PROCEDURE {drop_procedure_name}\
                ")