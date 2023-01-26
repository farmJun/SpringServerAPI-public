package site.lghtsg.api.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchMyAssetReq {
    private int transactionIdx;
    private String category;
}
