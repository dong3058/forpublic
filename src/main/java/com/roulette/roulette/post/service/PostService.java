package com.roulette.roulette.post.service;

import com.roulette.roulette.entity.Image;
import com.roulette.roulette.entity.Member;
import com.roulette.roulette.entity.Post;
import com.roulette.roulette.entity.PostImage;
import com.roulette.roulette.dto.post.AskPostRequestDto;
import com.roulette.roulette.dto.post.PostDto;
import com.roulette.roulette.dto.post.PostListDto;
import com.roulette.roulette.post.repository.ImageRepository;
import com.roulette.roulette.aboutlogin.repository.MemberJpaRepository;
import com.roulette.roulette.post.repository.PostImageRepository;
import com.roulette.roulette.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Value("${app.upload.dir}") // 외부 디렉토리 경로를 application.properties에서 가져옵니다.
    private String uploadDir;

    @Autowired
    private PostImageRepository postImageRepository;
    @Autowired
    private MemberJpaRepository memberJpaRepository; // MemberRepository를 주입받습니다.

    @Autowired
    private PostDtoService postDtoService;

    public Page<PostListDto> getRecentPosts(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<Post> posts = postRepository.findAll(pageable);

        return posts.map(postDtoService::convertToPostListDto);
        // 이 부분에 대한 설명 필요.

    }

    public Optional<PostDto> getPostById(Long postId){
        Optional<Post> optionalPost  = postRepository.findById(postId); // 포스트가 없을수도 있기 때문에
        return optionalPost.map(postDtoService::convertToPostDto);
    }

    public void setPostChoiceComplete(Long postId) {
        int updatedCount = postRepository.setPostChoiceTrue(postId);
        if (updatedCount == 0) {
            // 업데이트가 발생하지 않았을 경우, 게시글이 존재하지 않는 것일 수 있음
            throw new RuntimeException("No Post found with id: " + postId);
        }
    }

    public Long createPost(AskPostRequestDto requestDto) throws IOException {
        Member member = memberJpaRepository.findById(requestDto.getMember_id())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        //이 메소드는 나중에 수정해야함.

        Post post = new Post();

        post.setTitle(requestDto.getTitle());
        post.setContents(requestDto.getContents());
        post.setMember(member);
        post = postRepository.save(post);

        // Image와 PostImage 처리
        if (requestDto.getImage() != null) {
            Image image = storeImage(requestDto.getImage(), post);
            imageRepository.save(image);
        }

        return post.getPostId();
    }

    private Image storeImage(MultipartFile file, Post post) throws IOException {

        String fileName = "post_" + post.getPostId() + "_" + file.getOriginalFilename();; // 파일 이름 가져오기
        Path targetLocation = Paths.get(uploadDir).resolve(fileName);

        // 디렉토리가 없으면 생성
        if (!Files.exists(targetLocation.getParent())) {
            Files.createDirectories(targetLocation.getParent());
        }

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Create a new PostImage instance and save it
        PostImage postImage = new PostImage();
        postImage.setPost(post);
        postImage = postImageRepository.save(postImage);

        Image image = new Image();
        image.setImgUrl(targetLocation.toString());
        image.setPostImg(postImage); // Set the PostImage to the Image
        return image;
    }
}
