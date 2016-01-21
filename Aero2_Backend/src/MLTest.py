import DataBaseLayer as dl

data_base = dl.DataBaseLayer()

data = data_base.select_data("aero2.SampleDataTable")
print data