/* tslint:disable */
/* eslint-disable */
/**
 * PickGO API Document
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { mapValues } from "../runtime"
import type { PerformanceDetailResponse } from "./PerformanceDetailResponse"
import {
  PerformanceDetailResponseFromJSON,
  PerformanceDetailResponseFromJSONTyped,
  PerformanceDetailResponseToJSON,
  PerformanceDetailResponseToJSONTyped,
} from "./PerformanceDetailResponse"

/**
 *
 * @export
 * @interface PostDetailResponse
 */
export interface PostDetailResponse {
  /**
   *
   * @type {number}
   * @memberof PostDetailResponse
   */
  id?: number
  /**
   *
   * @type {string}
   * @memberof PostDetailResponse
   */
  title?: string
  /**
   *
   * @type {string}
   * @memberof PostDetailResponse
   */
  content?: string
  /**
   *
   * @type {boolean}
   * @memberof PostDetailResponse
   */
  isPublished?: boolean
  /**
   *
   * @type {number}
   * @memberof PostDetailResponse
   */
  views?: number
  /**
   *
   * @type {Date}
   * @memberof PostDetailResponse
   */
  createdAt?: Date
  /**
   *
   * @type {Date}
   * @memberof PostDetailResponse
   */
  modifiedAt?: Date
  /**
   *
   * @type {PerformanceDetailResponse}
   * @memberof PostDetailResponse
   */
  performance?: PerformanceDetailResponse
}

/**
 * Check if a given object implements the PostDetailResponse interface.
 */
export function instanceOfPostDetailResponse(
  value: object,
): value is PostDetailResponse {
  return true
}

export function PostDetailResponseFromJSON(json: any): PostDetailResponse {
  return PostDetailResponseFromJSONTyped(json, false)
}

export function PostDetailResponseFromJSONTyped(
  json: any,
  ignoreDiscriminator: boolean,
): PostDetailResponse {
  if (json == null) {
    return json
  }
  return {
    id: json["id"] == null ? undefined : json["id"],
    title: json["title"] == null ? undefined : json["title"],
    content: json["content"] == null ? undefined : json["content"],
    isPublished: json["isPublished"] == null ? undefined : json["isPublished"],
    views: json["views"] == null ? undefined : json["views"],
    createdAt:
      json["createdAt"] == null ? undefined : new Date(json["createdAt"]),
    modifiedAt:
      json["modifiedAt"] == null ? undefined : new Date(json["modifiedAt"]),
    performance:
      json["performance"] == null
        ? undefined
        : PerformanceDetailResponseFromJSON(json["performance"]),
  }
}

export function PostDetailResponseToJSON(json: any): PostDetailResponse {
  return PostDetailResponseToJSONTyped(json, false)
}

export function PostDetailResponseToJSONTyped(
  value?: PostDetailResponse | null,
  ignoreDiscriminator: boolean = false,
): any {
  if (value == null) {
    return value
  }

  return {
    id: value["id"],
    title: value["title"],
    content: value["content"],
    isPublished: value["isPublished"],
    views: value["views"],
    createdAt:
      value["createdAt"] == null ? undefined : value["createdAt"].toISOString(),
    modifiedAt:
      value["modifiedAt"] == null
        ? undefined
        : value["modifiedAt"].toISOString(),
    performance: PerformanceDetailResponseToJSON(value["performance"]),
  }
}
