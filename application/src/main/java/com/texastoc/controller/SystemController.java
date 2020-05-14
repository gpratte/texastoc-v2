package com.texastoc.controller;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

  private final Versions versions;

  public SystemController(@Value("${version.ui:#{null}}") String uiVersion) {
    versions = new Versions();
    versions.setUi(uiVersion);
  }

  @GetMapping("/api/v2/versions")
  public Versions getVersions() {
    return versions;
  }

  @Data
  @NoArgsConstructor
  static class Versions {
    private String ui;
  }
}
