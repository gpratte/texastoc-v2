package com.texastoc.controller;

import com.texastoc.repository.SystemRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

  private final SystemRepository systemRepository;

  public SystemController(SystemRepository systemRepository) {
    this.systemRepository = systemRepository;
  }

  @GetMapping("/api/v2/versions")
  public Versions getVersions() {
    Versions versions = new Versions();
    versions.setUi(systemRepository.get().getUiVersion());
    return versions;
  }

  @Data
  @NoArgsConstructor
  static class Versions {
    private String ui;
  }
}
