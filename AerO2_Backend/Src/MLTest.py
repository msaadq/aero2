import DataBaseLayer as dl
import Maps as maps
import pandas as pd

data_base = dl.DataBaseLayer()
map = maps.Maps()

data = data_base.select_data("aero2.SampleDataTable")
origins = [[data[i][2], data[i][1]] for i in range(1,len(data))]

info = [map.cal_data('AIzaSyCsVhDS_38NedVI7ZF37MOpkp7MbVxDDj8', origin) for origin in origins]
industries = [info[i][0] for i in range(0,len(info))]
traffic = [info[i][1] for i in range(0,len(info))]

print industries
print traffic 
print info

df = pd.DataFrame({
    'coordinates': origins,
    'industries': industries,
    'traffic': traffic,
    'smog':[data[i][4] for i in range(1,len(data))],
    'time':[data[i][5] for i in range(1,len(data))]
})

df.to_csv('..\Data\data.csv')