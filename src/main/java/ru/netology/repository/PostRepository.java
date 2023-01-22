package ru.netology.repository;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
// Stub
public class PostRepository{

  private final AtomicLong postID;
  private final ConcurrentHashMap<Long, Post> posts;

  public PostRepository() {
    postID = new AtomicLong(0);
    posts = new ConcurrentHashMap<>();
  }

  public List<Post> all() {
    ArrayList posts1 = (ArrayList) new ArrayList<>(posts.values()).stream()
            .filter(x -> !x.isRemoved())
            .collect(Collectors.toList());
    return posts1;
  }
  public Optional<Post> getById(long id) {
    if (posts.get(id).isRemoved() == false) {
      try {
        return Optional.ofNullable(posts.get(id));
      } catch (NotFoundException exception) {
        System.out.println(exception);
      }
    }
    return Optional.empty();
  }

  public Post save(Post post) {
    long postExistingID = post.getId();
    if (postExistingID > 0 && posts.containsKey(postExistingID)) {
      posts.replace(postExistingID, post);
    } else {
      long newPostId = postExistingID == 0 ? postID.incrementAndGet() : postExistingID;
      post.setId(newPostId);
      posts.put(newPostId, post);
    }
    return post;
  }

  public void removeById(long id) {
    posts.get(id).setRemoved(true);
  }
}
