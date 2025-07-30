#!/bin/bash

# Ubuntu Wallpaper Changer Uninstaller
set -e

echo "=== Ubuntu Wallpaper Changer Uninstaller ==="
echo "This script will remove the wallpaper changer app and all its components."
echo

# Get the current user
CURRENT_USER=$(whoami)
INSTALL_DIR="/opt/wallpaper-changer"
SERVICE_FILE="/etc/systemd/system/wallpaper-changer.service"
DESKTOP_FILE="/home/$CURRENT_USER/.local/share/applications/wallpaper-changer.desktop"

# Confirm uninstallation
read -p "Are you sure you want to uninstall the wallpaper changer? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Uninstallation cancelled."
    exit 0
fi

echo "Uninstalling wallpaper changer..."

# Stop and disable the service
if systemctl is-active --quiet wallpaper-changer; then
    echo "Stopping wallpaper-changer service..."
    sudo systemctl stop wallpaper-changer
fi

if systemctl is-enabled --quiet wallpaper-changer; then
    echo "Disabling wallpaper-changer service..."
    sudo systemctl disable wallpaper-changer
fi

# Remove service file
if [ -f "$SERVICE_FILE" ]; then
    echo "Removing systemd service file..."
    sudo rm "$SERVICE_FILE"
    sudo systemctl daemon-reload
fi

# Remove installation directory
if [ -d "$INSTALL_DIR" ]; then
    echo "Removing installation directory..."
    sudo rm -rf "$INSTALL_DIR"
fi

# Remove desktop file
if [ -f "$DESKTOP_FILE" ]; then
    echo "Removing desktop entry..."
    rm "$DESKTOP_FILE"
fi

# Clean up log file
if [ -f "/tmp/wallpaper_changer.log" ]; then
    echo "Removing log file..."
    rm "/tmp/wallpaper_changer.log"
fi

echo
echo "=== Uninstallation Complete ==="
echo "The wallpaper changer has been completely removed from your system."
echo
echo "If you installed any wallpapers specifically for this app, you may want to"
echo "clean them up from ~/Pictures or ~/Downloads manually."
echo