import DataTraining as dt

CITY_NAME = "Lahore"  # Default city name for AerO2 application

def run():

    data_training = dt.DataTraining()

    # Initializes the Database by saving all the required node properties
    data_training.initialize(CITY_NAME);

    # Updates the outputs in the database after interpolation
    # data_training.update_database()

