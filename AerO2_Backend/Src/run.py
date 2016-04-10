import sys, os
sys.path.append(os.path.join(os.getcwd(), "site-packages"))

import aero2.AzureRoutine as aero2
import urllib3

urllib3.disable_warnings()

aero2.run()