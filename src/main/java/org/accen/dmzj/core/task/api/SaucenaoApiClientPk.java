/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.accen.dmzj.core.task.api;

import java.io.File;
import java.util.stream.Collectors;

import org.accen.dmzj.core.task.api.vo.ImageResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SaucenaoApiClientPk {
	@Autowired
	private SaucenaoApiClient apiClient;
	
	public ImageResult search(File file) {
		String resp = apiClient.search(file);
		return parseSearchResult(resp);
	}
	private ImageResult parseSearchResult(String responseHtml) {
		ImageResult imageResult = new ImageResult();
		Document dom = Jsoup.parse(responseHtml);
		Elements similarityEles = dom.select("div.result:not(.hidden) table.resulttable div.resultsimilarityinfo");
		if(similarityEles!=null&&!similarityEles.isEmpty()) {
			imageResult.setSimilarity(similarityEles.first().text());
		}
		Elements titleEles = dom.select("div.result:not(.hidden) table.resulttable div.resulttitle");
		if(titleEles!=null&&!titleEles.isEmpty()) {
			imageResult.setTitle(titleEles.first().text());
		}
		Elements contentEles = dom.select("div.result:not(.hidden) table.resulttable div.resultcontentcolumn");
		if(contentEles!=null&&!contentEles.isEmpty()) {
			Elements contents = contentEles.first().children();
			String content = contents
				.stream()
				.filter(ctt->ctt.hasText())
				.map(ctt->("a".equals(ctt.tagName()))?ctt.text()+"：["+ctt.attr("href")+"]":ctt.text())
				.collect(Collectors.joining(" "));
			imageResult.setContent(content);
			imageResult.setSuccess(true);
		}
		return imageResult;
	}
}
