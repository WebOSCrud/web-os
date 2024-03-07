package cn.donting.web.os.core.db.entity;

import lombok.Data;

@Data
public class OsFileType {
    @Id
    String extName;
    String wapId;
    String iconResource;
    String description;
}
