import random 
import numpy as np
from skimage.transform import swirl
from scipy.ndimage import rotate
import albumentations as A

# Apply dropout to a single slice
def apply_dropout(self, img_slice, dropout_rate=0.1):
        
        mask = np.random.binomial(1, 1 - dropout_rate, img_slice.shape)
        return img_slice * mask

# Apply dropout to each slice of the image
def apply_dropout_to_image(self, img, dropout_rate=0.1, prob=0.5):
        dropout_img=img
        if random.random() < prob:
          dropout_img = np.array([[self.apply_dropout(img_slice, dropout_rate) for img_slice in img_channel] for img_channel in dropout_img])
        return dropout_img


# Apply random rotation to an image and its mask
def apply_rotation(self, img: np.ndarray, mask: np.ndarray, prob=0.5):
        rotated_img = img
        rotated_mask = mask
        if random.random() < prob:
          angle = random.uniform(-30, 60)  # Random angle between -30 and 60 degrees
          rotated_img = np.array([rotate(img_slice, angle, reshape=False) for img_slice in rotated_img])
          rotated_mask = np.array([rotate(mask_slice, angle, reshape=False) for mask_slice in rotated_mask])
        return rotated_img, rotated_mask


# Apply swirl transformation to a single slice
def apply_swirl_to_slice(self, img_slice, strength, radius):
        if img_slice.ndim == 3:
            img_slice = img_slice.squeeze()
        return swirl(img_slice, strength=strength, radius=radius)

# Apply swirl transformation to each slice of an image and its mask
def apply_swirl(self, img: np.ndarray, mask: np.ndarray, prob=0.5):
        
        swirled_img = img
        swirled_mask = mask
        if random.random() < prob:
          strength = random.uniform(0.2, 0.9)  # Swirl strength
          radius = random.uniform(100, 200)    # Swirl radius
          swirled_img = np.array([[self.apply_swirl_to_slice(slice, strength, radius) for slice in channel] for channel in swirled_img])
          swirled_mask = np.array([[self.apply_swirl_to_slice(slice, strength, radius) for slice in channel] for channel in swirled_mask])
        return swirled_img, swirled_mask


# Randomly adjust the contrast of an image with a given probability
def adjust_contrast(self, img: np.ndarray, contrast_range=(0.3, 0.7), prob=0.5):
        if random.random() < prob:
            contrast_factor = random.uniform(*contrast_range)
            img = img * contrast_factor
            img = np.clip(img, 0, 1)  
        return img


# Randomly adjust the brightness of an image with a given probability
def adjust_brightness(self, img: np.ndarray, brightness_range=(0.3, 0.7), prob=0.5):
       
        if random.random() < prob:
            if img.max() > 1:
                img = img / 255.0
            img_uint8 = (img * 255).astype(np.uint8)

            transform = A.Compose([
                A.RandomBrightness(limit=brightness_range, p=1)
            ])
            augmented = transform(image=img_uint8)
            img_aug = augmented['image']
            img_aug = img_aug.astype(np.float32) / 255
            img = img_aug
        return img


# Randomly flip the image and mask in the same way
def random_flip(self, img: np.ndarray, mask: np.ndarray,prob=0.5):
        img = img
        mask = mask
        if random.random() < prob:
            if random.choice([True, False]):  # Flip horizontally
                img = np.flip(img, axis=3)
                mask = np.flip(mask, axis=3)
            if random.choice([True, False]):  # Flip vertically
                img = np.flip(img, axis=2)
                mask = np.flip(mask, axis=2)
        return img, mask