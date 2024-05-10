package com.pvt.groupOne.controller;

import com.pvt.groupOne.model.UserImageRequest;
import com.pvt.groupOne.model.GroupImage;
import com.pvt.groupOne.model.GroupImageRequest;
import com.pvt.groupOne.model.UserImage;
import com.pvt.groupOne.repository.GroupImageRepository;
import com.pvt.groupOne.repository.UserImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/images")
@CrossOrigin
public class ImageController {

    @Autowired
    private UserImageRepository userImageRepository;

    @Autowired
    private GroupImageRepository groupImageRepository;

    @PostMapping(value = "/addUserImage")
    public @ResponseBody String addUserImage(@RequestBody UserImageRequest UserImageRequest) {
        String username = UserImageRequest.getUsername();
        String base64 = UserImageRequest.getBase64();
        if (userImageRepository.findByUserName(username) != null) {
            return "ERROR: Image already exists for user.";
        }

        UserImage myImage = new UserImage(username, base64);
        userImageRepository.save(myImage);
        return "Image for " + username + " successfully saved.";
    }

    @PostMapping(value = "/addGroupImage")
    public @ResponseBody String addGroupImage(@RequestBody GroupImageRequest groupImageRequest) {
        String groupName = groupImageRequest.getGroupName();
        if (groupImageRepository.findByGroupName(groupName) != null) {
            return "ERROR: Image already exists for group.";
        }
        String base64 = groupImageRequest.getBase64();
        GroupImage myImage = new GroupImage(groupName, base64);
        groupImageRepository.save(myImage);
        return "Image for group " + groupName + " successfully saved.";
    }

    @DeleteMapping(value = "/removeUserImage")
    public @ResponseBody String removeUserImage(@RequestParam String userName) {
        UserImage myImage = userImageRepository.findByUserName(userName);
        if (myImage == null) {
            return "ERROR: Image does not exist for user.";
        }

        userImageRepository.delete(myImage);
        return "Image for " + userName + " successfully removed.";
    }

    @DeleteMapping(value = "/removeGroupImage")
    public @ResponseBody String removeGroupImage(@RequestParam String groupName) {
        GroupImage myImage = groupImageRepository.findByGroupName(groupName);
        if (myImage == null) {
            return "ERROR: Image does not exist for group.";
        }

        groupImageRepository.delete(myImage);
        return "Image for " + groupName + " successfully removed.";
    }

    @PostMapping(value = "/updateUserImage")
    public @ResponseBody String updateUserImage(@RequestParam String userName, @RequestParam String base64) {
        UserImage myImage = userImageRepository.findByUserName(userName);
        if (myImage == null) {
            return "ERROR: Image does not exist for user.";
        }

        myImage.setBase64Image(base64);
        userImageRepository.save(myImage);
        return "Image for " + userName + " successfully updated.";
    }

    @PostMapping(value = "/updateGroupImage")
    public @ResponseBody String updateGroupImage(@RequestParam String groupName, @RequestParam String base64) {
        GroupImage myImage = groupImageRepository.findByGroupName(groupName);
        if (myImage == null) {
            return "ERROR: Image does not exist for group.";
        }

        myImage.setBase64Image(base64);
        groupImageRepository.save(myImage);
        return "Image for " + groupName + " successfully updated.";
    }

    @GetMapping(value = "/getUserImage/{userName}")
    public @ResponseBody String getUserImage(@PathVariable String userName) {
        UserImage myImage = userImageRepository.findByUserName(userName);
        if (myImage == null) {
            return "{\"image\": \"" + standardImage + "\"}";
        }
        String base64 = myImage.getBase64Image();
        return "{\"image\": \"" + base64 + "\"}";

    }

    String standardImage = "/9j/4AAQSkZJRgABAQEASABIAAD/4Qm9aHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLwA8P3hwYWNrZXQgYmVnaW49Iu+7vyIgaWQ9Ilc1TTBNcENlaGlIenJlU3pOVGN6a2M5ZCI/PiA8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIiB4OnhtcHRrPSJYTVAgQ29yZSA0LjQuMC1FeGl2MiI+IDxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+IDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+IDxkYzpjcmVhdG9yPiA8cmRmOlNlcT4gPHJkZjpsaT5WZWN0b3JTdG9jay5jb20vMTg2MjE5ODwvcmRmOmxpPiA8L3JkZjpTZXE+IDwvZGM6Y3JlYXRvcj4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgPD94cGFja2V0IGVuZD0idyI/Pv/bAEMABAMDAwMCBAMDAwQEBAUGCgYGBQUGDAgJBwoODA8ODgwNDQ8RFhMPEBURDQ0TGhMVFxgZGRkPEhsdGxgdFhgZGP/bAEMBBAQEBgUGCwYGCxgQDRAYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGP/AABEIAfQCOQMBIgACEQEDEQH/xAAdAAEAAgMAAwEAAAAAAAAAAAAACAkFBgcC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";
}
