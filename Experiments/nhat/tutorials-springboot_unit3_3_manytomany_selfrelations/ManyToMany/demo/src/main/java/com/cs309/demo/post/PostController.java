package com.cs309.demo.post;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/post")
public class PostController {

	@Autowired
	TagRepository tagRepo;

	@Autowired
	PostRepository postRepo;

	/**
	 * Creates a new Post object to be saved in the database.
	 *
	 * @return The ID of the Post
	 */
	@PostMapping("/create")
	public Long createPost(@RequestBody Post post) {
		if (postRepo.findByTitle(post.getTitle()) != null) {
			return null;
		}
		postRepo.save(post);
		return post.getId();
	}

	/**
	 * Returns all of the posts stored in the database.
	 *
	 * @return a List of all the posts in the database.
	 */
	@GetMapping("/getAll")
	public List<Post> getAllPosts() {
		return postRepo.findAll();
	}

	/**
	 * Returns the post associated with the given id.
	 *
	 * @param id
	 * @return Returns the post, null if it does not exist.
	 */
	@GetMapping("/get")
	public Post getPost(@RequestParam Long id) {
		if (postRepo.existsById(id)) {
			return postRepo.findById(id).get();
		} else {
			return null;
		}
	}

	/**
	 * Updates a post with the given title
	 *
	 * @param post
	 * @return
	 */
	@PutMapping("/update")
	public Long updatePost(@RequestBody Post post) {
		if (postRepo.findByTitle(post.getTitle()) == null) {
			return null;
		}
		post.setPostedAt(postRepo.findByTitle(post.getTitle()).getPostedAt());
		post.setLastUpdatedAt(new Date());
		postRepo.save(post);
		return post.getId();
	}

	/**
	 * Adds a tag to an already existing post
	 * @param tagName
	 * @param postID
	 */
	@PutMapping("/addTag")
	public void addTag(@RequestParam String tagName, @RequestParam Long postID) {
		Post post = postRepo.getOne(postID);
		if (post != null) {
			post.getTags().add(new Tag(tagName));
			post.setLastUpdatedAt(new Date());
			postRepo.save(post);
		}
	}
}
