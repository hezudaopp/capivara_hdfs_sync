package net.sf.jfilesync.gui.utils;

import javax.swing.JFrame;


/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Raik Nagel <raik.nagel@uni-bayreuth.de>
 *
 <license>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 </license>
 *
 */

public class TStartup {
    private static final SplashScreen splash = new SplashScreen();

    public static final void showSplashScreen() {
        if (splash != null) {
            splash.showSplash();
        }
    }

    public static final void hideSplashScreen() {
        if (splash != null) {
            splash.hideSplash();
        }
    }
    
    public static final void disposeSplashScreen() {
        if( splash != null ) {
            splash.disposeSplash();
        }
    }

    public static final void advanceSplashProgress() {
        if (splash != null) {
            splash.advance();
        }
    }

    public static final void advanceSplashProgress(String infoStr) {
        if (splash != null) {
            splash.advance(infoStr);
        }
    }

    public static JFrame getSplashScreenFrame() {
        return splash.getSplashFrame();
    }

}
