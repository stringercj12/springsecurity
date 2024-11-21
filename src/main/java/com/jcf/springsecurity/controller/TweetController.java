package com.jcf.springsecurity.controller;

import com.jcf.springsecurity.controller.dto.FeedDto;
import com.jcf.springsecurity.controller.dto.FeedItemDto;
import com.jcf.springsecurity.controller.dto.TweetCreateDto;
import com.jcf.springsecurity.entities.Role;
import com.jcf.springsecurity.entities.Tweet;
import com.jcf.springsecurity.repository.TweetRepository;
import com.jcf.springsecurity.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
public class TweetController {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDto> feed(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {


        var tweets = tweetRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp"))
                .map(tweet ->
                        new FeedItemDto(
                                tweet.getTweetId(),
                                tweet.getContent(),
                                tweet.getUser().getUsername())
                );

        return ResponseEntity.ok(new FeedDto(
                tweets.getContent(),
                page,
                pageSize,
                tweets.getTotalPages(),
                tweets.getTotalElements()
        ));
    }


    @PostMapping("/tweets")
    public ResponseEntity<Void> createTweet(@RequestBody TweetCreateDto dto,
                                            JwtAuthenticationToken token) {

        var user = userRepository.findById(UUID.fromString(token.getName()));

        if (!user.isPresent()) {
            throw new IllegalArgumentException("user not found");
        }

        Tweet tweet = new Tweet();
        tweet.setContent(dto.content());
        tweet.setUser(user.get());

        tweetRepository.save(tweet);

        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/tweets/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable("id") Long tweetId,
                                            JwtAuthenticationToken token) {

        var user = userRepository.findById(UUID.fromString(token.getName()));

        if (!user.isPresent()) {
            throw new IllegalArgumentException("user not found");
        }

        var tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var isAdmin = user.get().getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin || tweet.getUser().getUserId().equals(UUID.fromString(token.getName()))) {
            tweetRepository.deleteById(tweetId);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }

}
