# Ubuntu Wallpaper Changer

A simple and lightweight application that changes your desktop wallpaper when you press **Ctrl+Alt+W**.

## Features

- 🖼️ **Random Wallpaper Selection**: Automatically picks random wallpapers from your image directories
- ⌨️ **Global Hotkey**: Press `Ctrl+Alt+W` anywhere to change wallpaper
- 🔄 **Background Service**: Runs silently in the background
- 📁 **Multiple Sources**: Searches for wallpapers in common directories
- 🎨 **Format Support**: Supports JPG, PNG, BMP, GIF, and WebP formats
- 📝 **Logging**: Keeps track of changes and errors
- 🐧 **Ubuntu Optimized**: Designed specifically for Ubuntu/GNOME desktop

## Installation

### Quick Install

1. **Download the app**:
   ```bash
   git clone <repository-url>
   cd wallpaper-changer
   ```

2. **Run the installer**:
   ```bash
   chmod +x install.sh
   ./install.sh
   ```

3. **Follow the prompts** and the app will be installed and started automatically!

### Manual Installation

If you prefer to install manually:

```bash
# Install dependencies
sudo apt update
sudo apt install python3 python3-pip python3-venv

# Create installation directory
sudo mkdir -p /opt/wallpaper-changer

# Copy files
sudo cp wallpaper_changer.py /opt/wallpaper-changer/
sudo cp requirements.txt /opt/wallpaper-changer/

# Create virtual environment and install Python dependencies
sudo python3 -m venv /opt/wallpaper-changer/venv
sudo /opt/wallpaper-changer/venv/bin/pip install -r /opt/wallpaper-changer/requirements.txt
```

## Usage

### Key Combination
Press **Ctrl+Alt+W** to change your wallpaper to a random image.

### Service Management

Start the service:
```bash
sudo systemctl start wallpaper-changer
```

Enable auto-start on boot:
```bash
sudo systemctl enable wallpaper-changer
```

Stop the service:
```bash
sudo systemctl stop wallpaper-changer
```

Check service status:
```bash
sudo systemctl status wallpaper-changer
```

View real-time logs:
```bash
sudo journalctl -u wallpaper-changer -f
```

### Manual Execution

You can also run the app manually (useful for testing):
```bash
/opt/wallpaper-changer/venv/bin/python /opt/wallpaper-changer/wallpaper_changer.py
```

## Wallpaper Sources

The app automatically searches for wallpapers in these directories:
- `/usr/share/backgrounds` (system wallpapers)
- `/usr/share/pixmaps` (system images)
- `~/Pictures` (your Pictures folder)
- `~/Downloads` (your Downloads folder)

### Adding Your Own Wallpapers

Simply place your wallpaper images in any of the above directories. Supported formats:
- JPG/JPEG
- PNG
- BMP
- GIF
- WebP

## Troubleshooting

### App Not Responding to Key Combination

1. **Check if service is running**:
   ```bash
   sudo systemctl status wallpaper-changer
   ```

2. **Restart the service**:
   ```bash
   sudo systemctl restart wallpaper-changer
   ```

3. **Check logs for errors**:
   ```bash
   sudo journalctl -u wallpaper-changer -n 20
   ```

### No Wallpapers Found

1. **Add some images** to one of the wallpaper directories:
   ```bash
   # Download some sample wallpapers
   mkdir -p ~/Pictures/Wallpapers
   # Place your wallpaper images there
   ```

2. **Check the logs** to see what directories are being scanned:
   ```bash
   tail -f /tmp/wallpaper_changer.log
   ```

### Permission Issues

If you get permission errors:

1. **Check file permissions**:
   ```bash
   ls -la /opt/wallpaper-changer/
   ```

2. **Reinstall if necessary**:
   ```bash
   ./uninstall.sh
   ./install.sh
   ```

### Desktop Environment Compatibility

This app is designed for **GNOME/Ubuntu desktop**. If you're using a different desktop environment (KDE, XFCE, etc.), the wallpaper changing might not work as it uses GNOME's `gsettings`.

## Logs

The app creates logs in two places:
- **Service logs**: `sudo journalctl -u wallpaper-changer`
- **Application logs**: `/tmp/wallpaper_changer.log`

## Uninstallation

To completely remove the app:

```bash
./uninstall.sh
```

Or manually:
```bash
sudo systemctl stop wallpaper-changer
sudo systemctl disable wallpaper-changer
sudo rm /etc/systemd/system/wallpaper-changer.service
sudo rm -rf /opt/wallpaper-changer
rm ~/.local/share/applications/wallpaper-changer.desktop
sudo systemctl daemon-reload
```

## Technical Details

- **Language**: Python 3
- **Key Detection**: Uses `pynput` library for global hotkey detection
- **Wallpaper Setting**: Uses GNOME's `gsettings` command
- **Service**: Runs as a systemd user service
- **Dependencies**: Only `pynput` package required

## Contributing

Feel free to submit issues and enhancement requests!

## License

This project is open source and available under the MIT License.

---

**Enjoy your dynamic wallpapers!** 🎨✨