import DataBaseLayer as dbl

data_base = dbl.DataBaseLayer()

print data_base.select_data(data_base.SAMPLE_TABLE_NAME, data_base.key_range_string_gen("smog", 300, 320))