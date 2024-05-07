package com.pvt.groupOne.controller;

import com.pvt.groupOne.model.AddUserImageRequest;
import com.pvt.groupOne.model.GroupImage;
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
    public @ResponseBody String addUserImage(@RequestBody AddUserImageRequest addUserImageRequest) {
        String username = addUserImageRequest.getUsername();
        String base64 = addUserImageRequest.getBase64();
        if (userImageRepository.findByUserName(username) != null) {
            return "ERROR: Image already exists for user.";
        }

        UserImage myImage = new UserImage(username, base64);
        userImageRepository.save(myImage);
        return "Image for " + username + " successfully saved.";
    }

    @PostMapping(value = "/addGroupImage")
    public @ResponseBody String addGroupImage(@RequestParam String groupName, @RequestParam String base64) {
        if (groupImageRepository.findByGroupName(groupName) != null) {
            return "ERROR: Image already exists for group.";
        }
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
        if (userImageRepository.findByUserName(userName) == null) {
            return "ERROR: No image exists for user.";
        }
        UserImage myImage = userImageRepository.findByUserName(userName);
        String base64 = myImage.getBase64Image();

        return base64;
    }
}
