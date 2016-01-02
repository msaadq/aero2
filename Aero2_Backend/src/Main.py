import DataTraining as dt

data_train = dt.DataTraining()

#data_base.insert_multiple(data_base.PROP_TABLE_NAME, data_train._map_properties(data_train._save_all_properties("Islamabad")))

print data_train._map_properties(data_train._save_all_properties("Islamabad"))