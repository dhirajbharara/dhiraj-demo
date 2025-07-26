#!/usr/bin/env python3
from PIL import Image, ImageDraw
import os

def create_alarm_icon(size, filename):
    # Create a new image with transparent background
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Draw purple circle background
    draw.ellipse([2, 2, size-2, size-2], fill=(98, 0, 238, 255))
    
    # Calculate sizes relative to the icon size
    clock_size = size * 0.6
    clock_x = (size - clock_size) / 2
    clock_y = (size - clock_size) / 2
    
    # Draw white clock face
    draw.ellipse([clock_x, clock_y, clock_x + clock_size, clock_y + clock_size], 
                fill=(255, 255, 255, 255))
    
    # Draw clock hands
    center_x = size / 2
    center_y = size / 2
    
    # Hour hand (shorter, thicker)
    hand_length = size * 0.2
    draw.line([center_x, center_y, center_x, center_y - hand_length], 
             fill=(98, 0, 238, 255), width=max(1, size//24))
    
    # Minute hand (longer, thinner)
    hand_length = size * 0.25
    draw.line([center_x, center_y, center_x + hand_length, center_y], 
             fill=(98, 0, 238, 255), width=max(1, size//32))
    
    # Center dot
    dot_size = max(2, size//16)
    draw.ellipse([center_x - dot_size//2, center_y - dot_size//2, 
                 center_x + dot_size//2, center_y + dot_size//2], 
                fill=(98, 0, 238, 255))
    
    # Save the image
    img.save(filename, 'PNG')

# Create icons for different densities
densities = {
    'mdpi': 48,
    'hdpi': 72,
    'xhdpi': 96,
    'xxhdpi': 144,
    'xxxhdpi': 192
}

for density, size in densities.items():
    os.makedirs(f'app/src/main/res/mipmap-{density}', exist_ok=True)
    create_alarm_icon(size, f'app/src/main/res/mipmap-{density}/ic_launcher.png')
    create_alarm_icon(size, f'app/src/main/res/mipmap-{density}/ic_launcher_round.png')

print("Launcher icons generated successfully!")