package com.projectsmaneger.github.service;

import com.projectsmaneger.github.GitHubAccount;
import com.projectsmaneger.github.GitHubAccountRepository;
import com.projectsmaneger.github.dto.GitHubUserResponse;
import com.projectsmaneger.user.User;
import com.projectsmaneger.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GitHubAccountService {

  private final GitHubAccountRepository gitHubAccountRepository;
  private final UserRepository userRepository;

  public GitHubAccountService(
      GitHubAccountRepository gitHubAccountRepository, UserRepository userRepository) {
    this.gitHubAccountRepository = gitHubAccountRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public GitHubAccount linkAccount(
      String username, GitHubUserResponse githubUser, String accessToken) {

    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new IllegalStateException("ProjectsManeger user not found"));

    GitHubAccount existingGitHubAccount =
        gitHubAccountRepository.findByGithubUserId(githubUser.id()).orElse(null);

    if (existingGitHubAccount != null
        && !existingGitHubAccount.getUser().getId().equals(user.getId())) {
      throw new IllegalStateException("This GitHub account is already linked to another user");
    }

    GitHubAccount account =
        gitHubAccountRepository
            .findByUserId(user.getId())
            .orElseGet(
                () ->
                    new GitHubAccount(
                        user,
                        githubUser.id(),
                        githubUser.login(),
                        githubUser.name(),
                        githubUser.avatarUrl(),
                        accessToken));

    account.setUser(user);
    account.setGithubUserId(githubUser.id());
    account.setGithubLogin(githubUser.login());
    account.setDisplayName(githubUser.name());
    account.setAvatarUrl(githubUser.avatarUrl());
    account.setAccessToken(accessToken);

    return gitHubAccountRepository.save(account);
  }
}
