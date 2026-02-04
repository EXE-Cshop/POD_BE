package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.RenderRequest;
import com.shirt.pod.model.dto.response.RenderResponse;

public interface RenderEngineService {

    RenderResponse renderDesign(RenderRequest request);
}
