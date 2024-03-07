package cn.donting.web.os.core.db.entity;

import lombok.Data;

@Data
public class WapResource {
    @Id
    private String id;
    private String wapId;
    private String fileName;
}
