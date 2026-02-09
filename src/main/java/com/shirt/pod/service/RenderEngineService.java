package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.RenderPrintRequest;
import com.shirt.pod.model.dto.response.RenderResponse;

public interface RenderEngineService {

    /**
     * Render a production print file from millimeter-based layers.
     */
    RenderResponse renderPrintFile(RenderPrintRequest request);
}
