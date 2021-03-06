// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: heartbeat.proto

package com.xgama.sockethttpconvert.pb;

public final class HeartbeatClass {
  private HeartbeatClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface HeartbeatOrBuilder extends
      // @@protoc_insertion_point(interface_extends:sockethttpconvert.pb.Heartbeat)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required string appOrder = 1;</code>
     *
     * <pre>
     * 唯一标示
     * </pre>
     */
    boolean hasAppOrder();
    /**
     * <code>required string appOrder = 1;</code>
     *
     * <pre>
     * 唯一标示
     * </pre>
     */
    java.lang.String getAppOrder();
    /**
     * <code>required string appOrder = 1;</code>
     *
     * <pre>
     * 唯一标示
     * </pre>
     */
    com.google.protobuf.ByteString
        getAppOrderBytes();
  }
  /**
   * Protobuf type {@code sockethttpconvert.pb.Heartbeat}
   *
   * <pre>
   * 心跳包
   * </pre>
   */
  public static final class Heartbeat extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:sockethttpconvert.pb.Heartbeat)
      HeartbeatOrBuilder {
    // Use Heartbeat.newBuilder() to construct.
    private Heartbeat(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private Heartbeat(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final Heartbeat defaultInstance;
    public static Heartbeat getDefaultInstance() {
      return defaultInstance;
    }

    public Heartbeat getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private Heartbeat(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000001;
              appOrder_ = bs;
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.xgama.sockethttpconvert.pb.HeartbeatClass.internal_static_sockethttpconvert_pb_Heartbeat_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.xgama.sockethttpconvert.pb.HeartbeatClass.internal_static_sockethttpconvert_pb_Heartbeat_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat.class, com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat.Builder.class);
    }

    public static com.google.protobuf.Parser<Heartbeat> PARSER =
        new com.google.protobuf.AbstractParser<Heartbeat>() {
      public Heartbeat parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Heartbeat(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<Heartbeat> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    public static final int APPORDER_FIELD_NUMBER = 1;
    private java.lang.Object appOrder_;
    /**
     * <code>required string appOrder = 1;</code>
     *
     * <pre>
     * 唯一标示
     * </pre>
     */
    public boolean hasAppOrder() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required string appOrder = 1;</code>
     *
     * <pre>
     * 唯一标示
     * </pre>
     */
    public java.lang.String getAppOrder() {
      java.lang.Object ref = appOrder_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          appOrder_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string appOrder = 1;</code>
     *
     * <pre>
     * 唯一标示
     * </pre>
     */
    public com.google.protobuf.ByteString
        getAppOrderBytes() {
      java.lang.Object ref = appOrder_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        appOrder_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private void initFields() {
      appOrder_ = "";
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasAppOrder()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeBytes(1, getAppOrderBytes());
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, getAppOrderBytes());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code sockethttpconvert.pb.Heartbeat}
     *
     * <pre>
     * 心跳包
     * </pre>
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:sockethttpconvert.pb.Heartbeat)
        com.xgama.sockethttpconvert.pb.HeartbeatClass.HeartbeatOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.xgama.sockethttpconvert.pb.HeartbeatClass.internal_static_sockethttpconvert_pb_Heartbeat_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.xgama.sockethttpconvert.pb.HeartbeatClass.internal_static_sockethttpconvert_pb_Heartbeat_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat.class, com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat.Builder.class);
      }

      // Construct using com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        appOrder_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.xgama.sockethttpconvert.pb.HeartbeatClass.internal_static_sockethttpconvert_pb_Heartbeat_descriptor;
      }

      public com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat getDefaultInstanceForType() {
        return com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat.getDefaultInstance();
      }

      public com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat build() {
        com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat buildPartial() {
        com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat result = new com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.appOrder_ = appOrder_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat) {
          return mergeFrom((com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat other) {
        if (other == com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat.getDefaultInstance()) return this;
        if (other.hasAppOrder()) {
          bitField0_ |= 0x00000001;
          appOrder_ = other.appOrder_;
          onChanged();
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasAppOrder()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.xgama.sockethttpconvert.pb.HeartbeatClass.Heartbeat) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object appOrder_ = "";
      /**
       * <code>required string appOrder = 1;</code>
       *
       * <pre>
       * 唯一标示
       * </pre>
       */
      public boolean hasAppOrder() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required string appOrder = 1;</code>
       *
       * <pre>
       * 唯一标示
       * </pre>
       */
      public java.lang.String getAppOrder() {
        java.lang.Object ref = appOrder_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            appOrder_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string appOrder = 1;</code>
       *
       * <pre>
       * 唯一标示
       * </pre>
       */
      public com.google.protobuf.ByteString
          getAppOrderBytes() {
        java.lang.Object ref = appOrder_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          appOrder_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string appOrder = 1;</code>
       *
       * <pre>
       * 唯一标示
       * </pre>
       */
      public Builder setAppOrder(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        appOrder_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string appOrder = 1;</code>
       *
       * <pre>
       * 唯一标示
       * </pre>
       */
      public Builder clearAppOrder() {
        bitField0_ = (bitField0_ & ~0x00000001);
        appOrder_ = getDefaultInstance().getAppOrder();
        onChanged();
        return this;
      }
      /**
       * <code>required string appOrder = 1;</code>
       *
       * <pre>
       * 唯一标示
       * </pre>
       */
      public Builder setAppOrderBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        appOrder_ = value;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:sockethttpconvert.pb.Heartbeat)
    }

    static {
      defaultInstance = new Heartbeat(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:sockethttpconvert.pb.Heartbeat)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_sockethttpconvert_pb_Heartbeat_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_sockethttpconvert_pb_Heartbeat_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\017heartbeat.proto\022\024sockethttpconvert.pb\"" +
      "\035\n\tHeartbeat\022\020\n\010appOrder\030\001 \002(\tB0\n\036com.xg" +
      "ama.sockethttpconvert.pbB\016HeartbeatClass"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_sockethttpconvert_pb_Heartbeat_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_sockethttpconvert_pb_Heartbeat_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_sockethttpconvert_pb_Heartbeat_descriptor,
        new java.lang.String[] { "AppOrder", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
