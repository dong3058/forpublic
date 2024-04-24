package com.roulette.roulette.post.service;

import com.roulette.roulette.auditing.dto.post.PostAndReplyListDto;
import com.roulette.roulette.entity.Image;
import com.roulette.roulette.entity.Post;
import com.roulette.roulette.auditing.dto.post.PostListDto;
import com.roulette.roulette.entity.Reply;
import com.roulette.roulette.post.repository.ImageRepository;
import com.roulette.roulette.reply.repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostDtoService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ReplyRepository replyRepository;

    public PostAndReplyListDto convertToPostDto(Post post) {
        String imgBase64 = null;
        Optional<Image> optionalImage = imageRepository.findByPostImg_Post(post);
        //이미지 찾는 로직
        List<Reply> replies = replyRepository.findByPostPostId(post.getPostId());
        //reply 찾는 로직
        if (optionalImage.isPresent()) {
            try {
                Path imagePath = Paths.get(optionalImage.get().getImgUrl());
                imgBase64 = ImageConvertService.encodeFileToBase64Binary(imagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return new PostAndReplyListDto(
                post.getPostId(),
                post.getMember().getName(),
                post.getMember().getMemberId(),
                post.getTitle(),
                post.getContents(),
                imgBase64,
                post.getCreateTime(),
                replies
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
                post.getMember().getName(),
                post.getPostId(),
                post.getTitle(),
                post.getCreateTime(),
                imgBase64
        );
    }
}