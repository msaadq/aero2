import DataBaseLayer as dbl

database_layer = dbl.DataBaseLayer()

RESULTS_TABLE_NAME = "aero2.SampleDataTable"

where_string = " smog BETWEEN 4.0 AND 10.0"

print database_layer.delete_data(RESULTS_TABLE_NAME, where_string)
