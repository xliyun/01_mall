package com.cmcloud.search.repository;

import com.cmcloud.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsRepositiory extends ElasticsearchRepository<Goods,Long> {

}
