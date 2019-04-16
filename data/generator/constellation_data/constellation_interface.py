def export_config(bodies):
    content = __get_header() + '\n'
    for body in bodies['bodies']:
        for naif_id_key, value in body.items():
            s = '%-16s, ' % (value['name']) \
                + '% .17E, % .17E, ' % (value['mass'], value['radius']) \
                + '% .17E, % .17E, % .17E, ' % (value['position'][0], value['position'][1], value['position'][2]) \
                + '% .17E, % .17E, % .17E, ' % (value['speed'][0], value['speed'][1], value['speed'][2]) \
                + '255, 255, 255\n'
        content += s

        with open(r'bodies.txt', 'w') as the_file:
            the_file.write(content)


def __get_header():
    return """
    #Configuration:
    CALCULUS_METHOD:    1               # 0: basic, 1: jerk, 2:basic_Schwarzschild, 3: jerk_Schwarzschild
    SIMULATION_STEPS:   288000000
    STEPS_PER_PLOT:     2880            # 24*60*2
    STEP_TIME:          60              # Seconds
    START_EPOCH_TIME:   1520294400      # Epoc of 2018-Mar-06 00:00:00.0000 TDB. 
    METERS_PER_PIXEL:   1.0e-10         # UNIX Epoch time is the number of seconds that have elapsed since 
                                        # 00:00:00 Coordinated Universal Time (UTC), Thursday, 1 January 1970. 
                                        # [https://en.wikipedia.org/wiki/Unix_time]
    MAX_ORBIT_POINTS:   50000           # Avoid hard graphic computing, try to use less than 10000
    SCREEN_PERCENT:     50              # Size of screen in % 
    SCREEN_RESIZABLE:   true            # true or false
    COLOR_SCREEN:       21,21,21        # Screen color in R,G,B
    COLOR_DATE:         255,0,0         # Screen color in R,G,B
    COLOR_SCALE:        0,255,0         # Screen color in R,G,B
    COLOR_ANGLE:        102,255,255     # Screen color in R,G,B    

#   Name,    mass (Kg),   radio(m),               x0 (km) ,               y0 (km) ,                z0 (km),             vx0 (km/s),             vy0 (km/s),             vz0 (km/s),   R,   G,   B"""
