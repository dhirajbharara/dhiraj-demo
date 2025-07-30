#!/bin/bash

# Ubuntu Wallpaper Changer Installation Script
set -e

echo "=== Ubuntu Wallpaper Changer Installer ==="
echo "This script will install the wallpaper changer app and set it up as a system service."
echo

# Check if running on Ubuntu/Debian
if ! command -v apt &> /dev/null; then
    echo "Error: This installer is designed for Ubuntu/Debian systems with apt package manager."
    exit 1
fi

# Check if running as root
if [[ $EUID -eq 0 ]]; then
    echo "Error: Please run this script as a regular user (not root)."
    echo "The script will use sudo when needed."
    exit 1
fi

# Get the current user
CURRENT_USER=$(whoami)
INSTALL_DIR="/opt/wallpaper-changer"
SERVICE_FILE="/etc/systemd/system/wallpaper-changer.service"

echo "Installing for user: $CURRENT_USER"
echo "Installation directory: $INSTALL_DIR"
echo

# Update package list
echo "Updating package list..."
sudo apt update

# Install required system packages
echo "Installing required system packages..."
sudo apt install -y python3 python3-pip python3-venv

# Create installation directory
echo "Creating installation directory..."
sudo mkdir -p "$INSTALL_DIR"

# Copy application files
echo "Copying application files..."
sudo cp wallpaper_changer.py "$INSTALL_DIR/"
sudo cp requirements.txt "$INSTALL_DIR/"

# Create virtual environment
echo "Creating Python virtual environment..."
sudo python3 -m venv "$INSTALL_DIR/venv"

# Install Python dependencies
echo "Installing Python dependencies..."
sudo "$INSTALL_DIR/venv/bin/pip" install -r "$INSTALL_DIR/requirements.txt"

# Make the script executable
sudo chmod +x "$INSTALL_DIR/wallpaper_changer.py"

# Create the systemd service file
echo "Creating systemd service..."
sudo tee "$SERVICE_FILE" > /dev/null << EOF
[Unit]
Description=Wallpaper Changer Service
After=graphical-session.target
Wants=graphical-session.target

[Service]
Type=simple
User=$CURRENT_USER
Environment=DISPLAY=:0
Environment=XAUTHORITY=/home/$CURRENT_USER/.Xauthority
ExecStart=$INSTALL_DIR/venv/bin/python $INSTALL_DIR/wallpaper_changer.py
Restart=always
RestartSec=5

[Install]
WantedBy=default.target
EOF

# Create a desktop entry for manual launching
DESKTOP_FILE="/home/$CURRENT_USER/.local/share/applications/wallpaper-changer.desktop"
mkdir -p "/home/$CURRENT_USER/.local/share/applications"
tee "$DESKTOP_FILE" > /dev/null << EOF
[Desktop Entry]
Name=Wallpaper Changer
Comment=Change wallpaper with Ctrl+Alt+W
Exec=$INSTALL_DIR/venv/bin/python $INSTALL_DIR/wallpaper_changer.py
Icon=preferences-desktop-wallpaper
Terminal=false
Type=Application
Categories=Utility;
StartupNotify=false
EOF

chmod +x "$DESKTOP_FILE"

# Set proper ownership
sudo chown -R root:root "$INSTALL_DIR"
sudo chmod 755 "$INSTALL_DIR"

echo
echo "=== Installation Complete ==="
echo
echo "The wallpaper changer has been installed successfully!"
echo
echo "Key combination: Ctrl+Alt+W (to change wallpaper)"
echo
echo "Available commands:"
echo "  Start service:    sudo systemctl start wallpaper-changer"
echo "  Enable on boot:   sudo systemctl enable wallpaper-changer"
echo "  Stop service:     sudo systemctl stop wallpaper-changer"
echo "  Check status:     sudo systemctl status wallpaper-changer"
echo "  View logs:        sudo journalctl -u wallpaper-changer -f"
echo
echo "You can also run it manually: $INSTALL_DIR/venv/bin/python $INSTALL_DIR/wallpaper_changer.py"
echo
echo "The app will look for wallpapers in:"
echo "  - /usr/share/backgrounds"
echo "  - /usr/share/pixmaps"
echo "  - ~/Pictures"
echo "  - ~/Downloads"
echo
read -p "Would you like to start the service now? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    sudo systemctl daemon-reload
    sudo systemctl start wallpaper-changer
    sudo systemctl enable wallpaper-changer
    echo "Service started and enabled!"
    echo "Press Ctrl+Alt+W to test the wallpaper changer."
else
    echo "You can start the service later with: sudo systemctl start wallpaper-changer"
fi

echo
echo "Installation complete!"