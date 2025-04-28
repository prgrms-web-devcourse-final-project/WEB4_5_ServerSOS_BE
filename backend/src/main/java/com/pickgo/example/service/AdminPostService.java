package com.pickgo.example.service;

import com.pickgo.example.dto.PostSimpleResponse;
import com.pickgo.example.entity.Post;
import com.pickgo.example.repository.AdminPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPostService {

    private final AdminPostRepository adminPostRepository;

    public List<PostSimpleResponse> getAllPosts() {
        List<Post> posts = adminPostRepository.findAll();

        return posts.stream()
                .map(PostSimpleResponse::from)
                .collect(Collectors.toList());


    }
}
