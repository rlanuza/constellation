from . import *

# from .horizons_interface import show_bodies0, show_bodies1
# show_bodies1(10)
# show_bodies0()


bodies = load_bodies()

update_bodies(bodies)

#save_bodies(bodies)

export_config(bodies)
