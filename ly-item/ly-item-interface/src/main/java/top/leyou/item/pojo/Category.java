package top.leyou.item.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="tb_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
	@Id
	@KeySql(useGeneratedKeys = true)
	private Long id;
	private String name;
	private Long parentId;
	private Boolean isParent; // 注意isParent生成的getter和setter方法需要手动加上Is
	private Integer sort;

	public Category(Long id, String name) {
		this.id = id;
		this.name = name;
	}
}