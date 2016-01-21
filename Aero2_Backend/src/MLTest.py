import DataBaseLayer as dl
import Maps as maps
import pandas as pd

data_base = dl.DataBaseLayer()
map = maps.Maps()
origin = []

data = data_base.select_data("aero2.SampleDataTable")
print data[0]
origin.append(data[0][2])
origin.append(data[0][1])

df = map.calData(map.DEFAULT_GOOGLE_KEY_1,origin)