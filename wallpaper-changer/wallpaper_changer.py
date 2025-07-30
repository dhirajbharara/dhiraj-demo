#!/usr/bin/env python3
"""
Ubuntu Wallpaper Changer
A simple app that changes wallpaper when Ctrl+Alt+W is pressed.
"""

import os
import random
import subprocess
import sys
import threading
import time
from pathlib import Path
from pynput import keyboard
import logging

# Set up logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('/tmp/wallpaper_changer.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

class WallpaperChanger:
    def __init__(self):
        self.wallpaper_dirs = [
            '/usr/share/backgrounds',
            '/usr/share/pixmaps',
            os.path.expanduser('~/Pictures'),
            os.path.expanduser('~/Downloads'),
        ]
        self.supported_formats = {'.jpg', '.jpeg', '.png', '.bmp', '.gif', '.webp'}
        self.pressed_keys = set()
        self.target_combination = {keyboard.Key.ctrl_l, keyboard.Key.alt_l, keyboard.KeyCode.from_char('w')}
        logger.info("Wallpaper Changer initialized")
        logger.info("Press Ctrl+Alt+W to change wallpaper")

    def find_wallpapers(self):
        """Find all wallpaper images in the specified directories."""
        wallpapers = []
        
        for directory in self.wallpaper_dirs:
            if os.path.exists(directory):
                try:
                    for root, dirs, files in os.walk(directory):
                        for file in files:
                            if any(file.lower().endswith(ext) for ext in self.supported_formats):
                                wallpapers.append(os.path.join(root, file))
                except PermissionError:
                    logger.warning(f"Permission denied accessing {directory}")
                    continue
        
        logger.info(f"Found {len(wallpapers)} wallpaper images")
        return wallpapers

    def change_wallpaper(self, wallpaper_path):
        """Change the desktop wallpaper using gsettings."""
        try:
            # For GNOME/Ubuntu default desktop
            subprocess.run([
                'gsettings', 'set', 'org.gnome.desktop.background', 'picture-uri',
                f'file://{wallpaper_path}'
            ], check=True)
            
            # Also set for dark mode
            subprocess.run([
                'gsettings', 'set', 'org.gnome.desktop.background', 'picture-uri-dark',
                f'file://{wallpaper_path}'
            ], check=True)
            
            logger.info(f"Wallpaper changed to: {os.path.basename(wallpaper_path)}")
            return True
        except subprocess.CalledProcessError as e:
            logger.error(f"Failed to change wallpaper: {e}")
            return False

    def get_random_wallpaper(self):
        """Get a random wallpaper from available images."""
        wallpapers = self.find_wallpapers()
        if wallpapers:
            return random.choice(wallpapers)
        return None

    def on_key_press(self, key):
        """Handle key press events."""
        self.pressed_keys.add(key)
        
        # Check if our target combination is pressed
        if self.target_combination.issubset(self.pressed_keys):
            logger.info("Key combination detected: Ctrl+Alt+W")
            wallpaper = self.get_random_wallpaper()
            if wallpaper:
                self.change_wallpaper(wallpaper)
            else:
                logger.warning("No wallpapers found in specified directories")

    def on_key_release(self, key):
        """Handle key release events."""
        try:
            self.pressed_keys.discard(key)
        except KeyError:
            pass

    def run(self):
        """Start the wallpaper changer service."""
        logger.info("Starting wallpaper changer service...")
        logger.info("Monitoring for Ctrl+Alt+W key combination")
        
        # Test if we can find wallpapers
        wallpapers = self.find_wallpapers()
        if not wallpapers:
            logger.warning("No wallpapers found! Please ensure you have images in:")
            for directory in self.wallpaper_dirs:
                logger.warning(f"  - {directory}")
        
        try:
            with keyboard.Listener(
                on_press=self.on_key_press,
                on_release=self.on_key_release
            ) as listener:
                listener.join()
        except Exception as e:
            logger.error(f"Error starting keyboard listener: {e}")
            sys.exit(1)

def main():
    """Main entry point."""
    try:
        changer = WallpaperChanger()
        changer.run()
    except KeyboardInterrupt:
        logger.info("Wallpaper changer stopped by user")
    except Exception as e:
        logger.error(f"Unexpected error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()