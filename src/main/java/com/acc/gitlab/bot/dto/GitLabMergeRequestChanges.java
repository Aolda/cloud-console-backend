package com.acc.gitlab.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class GitLabMergeRequestChanges {
    private Long id;
    private Long iid;
    private String title;
    private String description;
    private List<Change> changes;

    @Data
    public static class Change {
        @JsonProperty("old_path")
        private String oldPath;
        @JsonProperty("new_path")
        private String newPath;
        private String diff;
        @JsonProperty("new_file")
        private Boolean newFile;
        @JsonProperty("renamed_file")
        private Boolean renamedFile;
        @JsonProperty("deleted_file")
        private Boolean deletedFile;
    }
}