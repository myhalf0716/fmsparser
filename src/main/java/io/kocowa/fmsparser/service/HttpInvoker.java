package io.kocowa.fmsparser.service;

import io.kocowa.fmsparser.vo.FmsQueryVO;

public interface HttpInvoker<T> {
  T invoke(FmsQueryVO query);
}
