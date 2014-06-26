/*
 * Copyright 2012 Global Biodiversity Information Facility (GBIF)
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
package org.gbif.api.model.common.search;

import org.gbif.api.model.common.paging.Pageable;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Generic request class for search operations requesting facets.
 * It extends a search request with a list of desired facets and optional settings.
 */
public class FacetedSearchRequest<P extends SearchParameter> extends SearchRequest<P> {

  private Set<P> facets = Sets.newHashSet();
  private boolean facetsOnly;
  private boolean multiSelectFacets;
  private Integer facetMinCount;

  public FacetedSearchRequest() {
  }

  public FacetedSearchRequest(Pageable page) {
    super(page);
  }

  public FacetedSearchRequest(SearchRequest<P> searchRequest) {
    super(searchRequest);
    setHighlight(searchRequest.isHighlight());
    setParameters(searchRequest.getParameters());
    setQ(searchRequest.getQ());
  }

  public FacetedSearchRequest(long offset, int limit) {
    super(offset, limit);
  }

  public FacetedSearchRequest(long offset, int limit, boolean facetsOnly) {
    super(offset, limit);
    this.facetsOnly = facetsOnly;
  }

  /**
   * Min count of facet to return, if the facet count is less than this number the facet won't be included.
   */
  public Integer getFacetMinCount() {
    return facetMinCount;
  }

  public void setFacetMinCount(Integer facetMinCount) {
    this.facetMinCount = facetMinCount;
  }

  /**
   * Gets the list of requested facets by the search operation.
   * The facets are a list of search parameters.
   */
  public Set<P> getFacets() {
    return facets;
  }

  /**
   * Sets the list of facets.
   */
  public void setFacets(Set<P> facets) {
    this.facets = facets;
  }

  /**
   * This flag indicates if only facets information must be included in the response.
   *
   * @return the facetsOnly
   */
  public boolean isFacetsOnly() {
    return facetsOnly;
  }

  /**
   * @param facetsOnly the isFaceOnly to set.
   */
  public void setFacetsOnly(boolean facetsOnly) {
    this.facetsOnly = facetsOnly;
  }

  /**
   * @return the multiSelectFacets
   */
  public boolean isMultiSelectFacets() {
    return multiSelectFacets;
  }

  /**
   * @param multiSelectFacets the multiSelectFacets to set
   */
  public void setMultiSelectFacets(boolean multiSelectFacets) {
    this.multiSelectFacets = multiSelectFacets;
  }


  public void addFacets(P... facets) {
    if (this.facets == null) {
      this.facets = Sets.newHashSet(facets);
    } else {
      this.facets.addAll(Sets.newHashSet(facets));
    }
  }

}