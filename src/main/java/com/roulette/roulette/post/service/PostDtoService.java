package com.roulette.roulette.post.service;

import com.roulette.roulette.entity.Image;
import com.roulette.roulette.entity.Post;
import com.roulette.roulette.dto.post.PostDto;
import com.roulette.roulette.dto.post.PostListDto;
import com.roulette.roulette.post.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Transactional
public class PostDtoService {

    @Autowired
    private ImageRepository imageRepository;

    public PostDto convertToPostDto(Post post) {
        String imgBase64 = null;
        Optional<Image> optionalImage = imageRepository.findByPostImg_Post(post);
        if (optionalImage.isPresent()) {
            try {
                Path imagePath = Paths.get(optionalImage.get().getImgUrl());
                imgBase64 = ImageConvertService.encodeFileToBase64Binary(imagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return new PostDto(
                post.getPostId(),
                post.getMember().getMemberId(),
                post.getContents(),
                imgBase64
        );
    }

    public PostListDto convertToPostListDto(Post post) {
        String imgBase64 = null;
        Optional<Image> optionalImage = imageRepository.findByPostImg_Post(post);
        if (optionalImage.isPresent()) {
            try {
                Path imagePath = Paths.get(optionalImage.get().getImgUrl());
                imgBase64 = ImageConvertService.encodeFileToBase64Binary(imagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return new PostListDto(
                post.getMember().getMemberId(),
                post.getPostId(),
                post.getTitle(),
                post.getCreateTime().toString(),
                imgBase64
        );
    }
}