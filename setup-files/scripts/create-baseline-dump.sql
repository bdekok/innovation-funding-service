# A script to generate a dump from a database without any of the reference data tables in it

mysqldump -h ifs-database ifs -uroot -ppassword --no-create-info --extended-insert=false --ignore-table=ifs.schema_version --ignore-table=ifs.activity_state --ignore-table=ifs.finance_row_meta_field --ignore-table=ifs.address_type --ignore-table=ifs.application_status --ignore-table=ifs.category --ignore-table=ifs.form_validator --ignore-table=ifs.form_input_type --ignore-table=ifs.organisation_type --ignore-table=ifs.participant_status --ignore-table=ifs.project_role --ignore-table=ifs.role --ignore-table=ifs.competition_type --ignore-table=ifs.academic --ignore-table=ifs.contract --ignore-table=ifs.ethnicity --ignore-table=ifs.rejection_reason > ../../ifs-data-service/src/main/resources/db/webtest/V81_2_6__Base_webtest_data.sql