package de.adorsys.opba.consent.rest.api.domain;

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Single message to be displayed to the PSU.")
public class PsuMessage {
  @JsonProperty("category")
  private PsuMessageCategory category = null;
  @JsonProperty("code")
  private String code = null;
  @JsonProperty("path")
  private String path = null;
  @JsonProperty("text")
  private String text = null;

  public PsuMessage category(PsuMessageCategory category) {
    this.category = category;
    return this;
  }

  /**
   * Get category
   *
   * @return category
   **/
  @ApiModelProperty(required = true, value = "")
  @NotNull
  @Valid
  public PsuMessageCategory getCategory() {
    return category;
  }

  public void setCategory(PsuMessageCategory category) {
    this.category = category;
  }

  public PsuMessage code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   *
   * @return code
   **/
  @ApiModelProperty(required = true, value = "")
  @NotNull
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public PsuMessage path(String path) {
    this.path = path;
    return this;
  }

  /**
   * Get path
   *
   * @return path
   **/
  @ApiModelProperty(value = "")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public PsuMessage text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   *
   * @return text
   **/
  @ApiModelProperty(value = "")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PsuMessage psuMessage = (PsuMessage) o;
    return Objects.equals(this.category, psuMessage.category) && Objects.equals(this.code, psuMessage.code)
        && Objects.equals(this.path, psuMessage.path) && Objects.equals(this.text, psuMessage.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(category, code, path, text);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PsuMessage {\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
