package com.tadashop.nnt.service;

import java.util.List;

import com.tadashop.nnt.dto.SizeDto;
import com.tadashop.nnt.dto.SizeResp;
import com.tadashop.nnt.model.Size;

public interface SizeService {
    Size saveSize(SizeDto dto);

    SizeDto updateSize(Long id, SizeDto dto);

    void deleteSizeById(Long id);

    Size getSizeById(Long id);

    List<SizeResp> findAll();
    
	List<Size> findAllSizeByProduct(Long id);
}
