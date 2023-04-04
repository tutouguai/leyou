package top.leyou.item.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_stock")
@Data
@NoArgsConstructor
public class Stock {

    @Id
    private Long skuId;
    private Integer seckillStock;// 秒杀可用库存
    private Integer seckillTotal;// 已秒杀数量
    private Integer stock;// 正常库存

    public Stock(Long skuId, Integer stock){
        this.skuId = skuId;
        this.stock = stock;
    }

}